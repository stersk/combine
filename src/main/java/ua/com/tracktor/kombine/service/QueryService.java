package ua.com.tracktor.kombine.service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import ua.com.tracktor.kombine.data.QueryRepository;
import ua.com.tracktor.kombine.entity.Query;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@EnableAsync
public class QueryService {
    @Autowired
    QueryRepository queryRepository;

    @Autowired
    Environment environment;

    @Autowired
    PropertyService propertyService;

    @Autowired
    UserService userService;

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, 1,
            0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2));

    private class ProcessDelayedQueriesTask implements Runnable, AutoCloseable {
        private volatile boolean interruptExecution = false;

        @Override
        public void run() {
            boolean processQueries = Boolean.parseBoolean(propertyService.getProperty("viber-service.delayed-queries-processing.enabled"));

            if (processQueries) {
                List<Query> queriesForProcess = getQueriesToProcess();
                if (!queriesForProcess.isEmpty()) {
                    queriesForProcess.forEach(this::processQuery);

                    runDelayedQueryProcessing();
                }
            }
        }

        @Override
        public void close() {
            interruptExecution = true;
        }

        private void processQuery(Query query) {
            if (!interruptExecution) {

                try {
                    String account = query.getAccount();
                    String scheme = environment.getProperty("viber-service.server.scheme");
                    String address = environment.getProperty("viber-service.server.address");
                    String path = environment.getProperty("viber-service.server.path") + query.getAccount();
                    int port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("viber-service.server.port")));

                    URI uri = new URI(scheme, null, address, port, path, null, null);

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("X-Viber-Content-Signature", query.getSignature());
                    headers.add("X-Delayed-Content", "true");

                    userService.addBasicAuthHeader(account, headers);

                    HttpEntity<String> httpEntity = new HttpEntity<>(query.getRequestBody(), headers);
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters()
                            .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // for correct cyrillic symbols in string body

                    ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
                    if (responseEntity.getStatusCode() == HttpStatus.OK) {
                        query.setProcessingResultCode(HttpStatus.OK);
                        query.setProcessingError(false);
                        query.setProcessingDate(new Timestamp(System.currentTimeMillis()));
                        query.setProcessingErrorMessage("");

                    } else {
                        query.setProcessingResultCode(responseEntity.getStatusCode());
                        query.setProcessingErrorMessage(responseEntity.getBody());
                    }

                } catch (Exception e) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(e.getLocalizedMessage()).append(System.lineSeparator()).append(System.lineSeparator());
                    stringBuilder.append(ExceptionUtils.getStackTrace(e));

                    HttpStatus respondStatus = null;

                    if (e instanceof HttpClientErrorException) {
                        respondStatus =  HttpStatus.resolve(((HttpClientErrorException) e).getRawStatusCode());
                    }

                    if (respondStatus == null) {
                        respondStatus = HttpStatus.I_AM_A_TEAPOT;
                    }

                    query.setProcessingResultCode(respondStatus);
                    query.setProcessingErrorMessage(stringBuilder.toString());
                }
                queryRepository.save(query);
            }
        }

        private List<Query> getQueriesToProcess() {
            List<Query> queries = queryRepository.findTop10ByProcessingResultCodeOrderByRequestDateDesc(0);
            if (queries.size() < 10) {
                Timestamp dateForLimit = new Timestamp(System.currentTimeMillis() - 86400000L);

                List<Query> queriesWithError = queryRepository.findTop10ByProcessingResultCodeNotAndRetryTrueAndProcessingDateBeforeOrderByRequestDateDesc(0, dateForLimit);
                if (queriesWithError.size() > 0) {
                    queries = Stream.concat(queries.stream(), queriesWithError.stream()).limit(10).collect(Collectors.toList());
                }
            }

            return queries;
        }
    }

    public Query saveQuery(String signature, String account, String body, String messageType, String messageToken, String messageUserId) {
        Query query = new Query();
        query.setSignature(signature);
        query.setAccount(account);
        query.setRequestBody(body);
        query.setRequestDate(new Timestamp(System.currentTimeMillis()));
        query.setProcessingResultCode(null);
        query.setMessageType(messageType);
        query.setMessageToken(messageToken);
        query.setMessageUserId(messageUserId);

        query = queryRepository.save(query);

        return query;
    }

    public Timestamp getDelayedMessageDateIfExist(String messageType, String messageToken, String messageUserId) {
        Timestamp result = null;

        Optional<Query> query = queryRepository.findByMessageTypeAndMessageTokenAndMessageUserId(messageType, messageToken, messageUserId);
        if (query.isPresent()) {
            result = query.get().getRequestDate();
        }

        return result;
    }

    public void runDelayedQueryProcessing() {
        if (!threadPoolExecutor.isShutdown()) {
            if (threadPoolExecutor.getQueue().remainingCapacity() > 0) {
                try {
                    threadPoolExecutor.execute(new ProcessDelayedQueriesTask());
                } catch (RejectedExecutionException exception) {
                    System.out.println(exception.getMessage());
                }


            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Async
    void deleteProcessedQueries() {
        long daysOffset = Long.parseLong(Objects.requireNonNull(propertyService.getProperty("viber-service.delayed-queries-processing.days-to-keep-processed-queries")));
        List<Query> queriesToDelete = queryRepository.findByProcessingResultCodeAndProcessingDateBefore(200, new Timestamp(System.currentTimeMillis() - daysOffset * 86400000));
        queryRepository.deleteAll(queriesToDelete);
    }
}

package ua.com.tracktor.kombine.service;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ua.com.tracktor.kombine.data.QueryRepository;
import ua.com.tracktor.kombine.entity.Query;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class QueryService {
    @Autowired
    QueryRepository queryRepository;

    @Autowired
    Environment environment;

    @Autowired
    PropertyService propertyService;

    private static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, 1,
            0L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2));

    private class ProcessDelayedQueriesTask implements Runnable, AutoCloseable {
        private volatile boolean interruptExecution = false;

        @Override
        public void run() {
            boolean processQueries = Boolean.parseBoolean(propertyService.getProperty("viber-service.delayed-queries-processing.enabled"));

            if (processQueries) {
                List<Query> queriesForProcess = queryRepository.findTop10ByProcessingErrorFalseOrderByIdDesc();
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
                    String scheme = environment.getProperty("viber-service.server.scheme");
                    String address = environment.getProperty("viber-service.server.address");
                    String path = environment.getProperty("viber-service.server.path") + query.getAccount();
                    int port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("viber-service.server.port")));

                    URI uri = new URI(scheme, null, address, port, path, null, null);

                    HttpHeaders headers = new HttpHeaders();
                    headers.add("X-Viber-Content-Signature", query.getSignature());
                    headers.add("X-Delayed-Content", "true");

                    HttpEntity<String> httpEntity = new HttpEntity<>(query.getRequestBody(), headers);
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters()
                            .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // for correct cyrillic symbols in string body

                    ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
                    if (responseEntity.getStatusCode() == HttpStatus.OK) {
                        queryRepository.delete(query);
                    } else {
                        query.setProcessingResultCode(responseEntity.getStatusCode());
                        query.setProcessingErrorMessage(responseEntity.getBody());
                    }

                } catch (Exception e) {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(e.getLocalizedMessage()).append(System.lineSeparator()).append(System.lineSeparator());
                    stringBuilder.append(ExceptionUtils.getStackTrace(e));

                    query.setProcessingResultCode(HttpStatus.I_AM_A_TEAPOT);
                    query.setProcessingErrorMessage(stringBuilder.toString());
                    queryRepository.save(query);
                }
            }
        }
    }

    public Query saveQuery(String signature, String account, String body) {
        long currentMillis = System.currentTimeMillis();

        Query query = new Query();
        query.setSignature(signature);
        query.setAccount(account);
        query.setRequestBody(body);
        query.setRequestDate(new Timestamp(currentMillis));
        query.setProcessingResultCode(null);

        return queryRepository.save(query);
    }

    public void runDelayedQueryProcessing() {
        if (!threadPoolExecutor.isShutdown()) {
            if (threadPoolExecutor.getQueue().remainingCapacity() > 0) {
                threadPoolExecutor.execute(new ProcessDelayedQueriesTask());
            }
        }
    }
}

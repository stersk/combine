package ua.com.tracktor.kombine.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ua.com.tracktor.kombine.service.QueryService;
import ua.com.tracktor.kombine.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class QueryController {
    @Autowired
    Environment environment;

    @Autowired
    QueryService queryService;

    @Autowired
    UserService userService;

    // used for Viber webhook processing
    @PostMapping(path="/viber/{account}")
    public ResponseEntity<String> processViberRequest(@RequestBody String body, @RequestHeader HttpHeaders headers, HttpServletRequest request, @PathVariable String account) throws URISyntaxException {
        ResponseEntity<String> responseEntity;

        ObjectMapper mapper = new ObjectMapper();
        boolean proxyOnly = true;
        String messageType = "";
        String messageToken = "";
        String messageUserId = "";

        long startTimeInMillis = System.currentTimeMillis();

        try {
            JsonNode responseBodyNode = mapper.readTree(body);

            messageToken = responseBodyNode.get("message_token").asText();
            messageType = responseBodyNode.get("event").asText();

            if (responseBodyNode.has("user_id")) {
                messageUserId = responseBodyNode.get("user_id").asText();
            }

            switch (messageType) {
                case "delivered":
                case "seen":
                case "failed":
                    proxyOnly   = false;
                    messageType = responseBodyNode.get("event").asText();
                    break;

                default:
                    proxyOnly = true;

            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (proxyOnly) {
            String scheme = environment.getProperty("viber-service.server.scheme");
            String server = environment.getProperty("viber-service.server.address");
            String basePath = environment.getProperty("viber-service.server.path");
            int port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("viber-service.server.port")));
            String path = basePath + account;

            URI uri = new URI(scheme, null, server, port, path, null, null);

            if (headers.getFirst("Authorization") == null) {
                userService.addBasicAuthHeader(account, headers);
            }

            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters()
                        .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // for correct cyrillic symbols in string body
                responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                String responseBody = e.getResponseBodyAsString();
                responseEntity = new ResponseEntity<>(responseBody, e.getStatusCode());
            } catch (Exception e) {
                Map<String, String> data = new HashMap<>();
                data.put("error", "Query send error");
                data.put("reason", e.getLocalizedMessage());

                ObjectMapper objectMapper = new ObjectMapper();
                String stringData;
                try {
                    stringData = objectMapper.writeValueAsString(data);
                } catch (JsonProcessingException jsonProcessingException) {
                    stringData = e.getLocalizedMessage();
                }

                responseEntity = new ResponseEntity<>(stringData, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } else {
            String signature = headers.getFirst("X-Viber-Content-Signature");
            Timestamp originalQueryTimestamp = queryService.getDelayedMessageDateIfExist(messageType, messageToken, messageUserId);
            if (originalQueryTimestamp == null) {
                queryService.saveQuery(signature, account, body, messageType, messageToken, messageUserId);
                queryService.runDelayedQueryProcessing();
            }
            responseEntity = new ResponseEntity<>("", HttpStatus.OK);
        }

        return responseEntity;
    }

    // used for Telegram webhook processing
    @RequestMapping(path="/telegram/{account}")
    public ResponseEntity<String> processTelegramRequest(@RequestBody String body, @RequestHeader HttpHeaders headers, HttpServletRequest request, @PathVariable String account) throws URISyntaxException {
        ResponseEntity<String> responseEntity = null;

        ObjectMapper mapper = new ObjectMapper();
        String messageType = "";
        String messageToken = "";
        String messageUserId = "";
        boolean proxyOnly = true;

        long startTimeInMillis = System.currentTimeMillis();

        if (proxyOnly) {
            String scheme = environment.getProperty("viber-service.server.scheme");
            String server = environment.getProperty("viber-service.server.address");
            String basePath = environment.getProperty("viber-service.server.path");
            int port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("viber-service.server.port")));
            String path = basePath + account;

            URI uri = new URI(scheme, null, server, port, path, null, null);

            if (headers.getFirst("Authorization") == null) {
                userService.addBasicAuthHeader(account, headers);
            }

            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters()
                        .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // for correct cyrillic symbols in string body
                responseEntity = restTemplate.exchange(uri, HttpMethod.valueOf(request.getMethod()), httpEntity, String.class);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                String responseBody = e.getResponseBodyAsString();
                responseEntity = new ResponseEntity<>(responseBody, e.getStatusCode());
            } catch (Exception e) {
                Map<String, String> data = new HashMap<>();
                data.put("error", "Query send error");
                data.put("reason", e.getLocalizedMessage());

                ObjectMapper objectMapper = new ObjectMapper();
                String stringData;
                try {
                    stringData = objectMapper.writeValueAsString(data);
                } catch (JsonProcessingException jsonProcessingException) {
                    stringData = e.getLocalizedMessage();
                }

                responseEntity = new ResponseEntity<>(stringData, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        return responseEntity;
    }

    @PostMapping(path="/hs/viberService/**")
    public ResponseEntity<String> processClientRequest(@RequestBody String body, @RequestHeader HttpHeaders headers, HttpServletRequest request) throws URISyntaxException {
        // Temporary hardcoded
        String account = "e3c5fbdf-bd57-11eb-a0a7-4ccc6af41fd6";
        if (request.getRequestURI().contains("24a93fd5-2538-11ec-a232-00155d008d05")) {
            account = "765845de-24e6-11ec-a0dc-4ccc6af41fd6";
        }

        ResponseEntity<String> responseEntity;

        String scheme = environment.getProperty("viber-service.server.scheme");
        String server = environment.getProperty("viber-service.server.address");
        String basePath = environment.getProperty("viber-service.server.path");

        //TODO make basePath without webservice
        StringBuilder sb = new StringBuilder(basePath);
        sb.setLength(sb.length() - 12);
        sb.append("viberService/");
        basePath = sb.toString();

        int port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("viber-service.server.port")));
        String path = basePath + request.getRequestURI().substring(16);

        URI uri = new URI(scheme, null, server, port, path, null, null);

        if (headers.getFirst("Authorization") == null) {
            userService.addBasicAuthHeader(account, headers);
        }

        HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // for correct cyrillic symbols in string body
            responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            responseEntity = new ResponseEntity<>(responseBody, e.getStatusCode());
        } catch (Exception e) {
            Map<String, String> data = new HashMap<>();
            data.put("error", "Query send error");
            data.put("reason", e.getLocalizedMessage());

            ObjectMapper objectMapper = new ObjectMapper();
            String stringData;
            try {
                stringData = objectMapper.writeValueAsString(data);
            } catch (JsonProcessingException jsonProcessingException) {
                stringData = e.getLocalizedMessage();
            }

            responseEntity = new ResponseEntity<>(stringData, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }

    @GetMapping(path="/hs/viberService/**")
    public ResponseEntity<String> processGetClientRequest(@RequestHeader HttpHeaders headers, HttpServletRequest request) throws URISyntaxException {
        // Temporary hardcoded
        String account = "e3c5fbdf-bd57-11eb-a0a7-4ccc6af41fd6";

        ResponseEntity<String> responseEntity;

        String scheme = environment.getProperty("viber-service.server.scheme");
        String server = environment.getProperty("viber-service.server.address");
        String basePath = environment.getProperty("viber-service.server.path");

        //TODO make basePath without webservice
        StringBuilder sb = new StringBuilder(basePath);
        sb.setLength(sb.length() - 12);
        sb.append("viberService/");
        basePath = sb.toString();

        int port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("viber-service.server.port")));
        String path = basePath + request.getRequestURI().substring(16);

        Map<String, String[]> requestParameters = request.getParameterMap();
        URI uri = new URI(scheme, null, server, port, path, null, null);

        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri);
        requestParameters.forEach((k, v) -> builder.queryParam(k, v));
        uri = builder.build().toUri();

        if (headers.getFirst("Authorization") == null) {
            userService.addBasicAuthHeader(account, headers);
        }

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters()
                    .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // for correct cyrillic symbols in string body
            responseEntity = restTemplate.exchange(uri, HttpMethod.GET, httpEntity, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            String responseBody = e.getResponseBodyAsString();
            responseEntity = new ResponseEntity<>(responseBody, e.getStatusCode());
        } catch (Exception e) {
            Map<String, String> data = new HashMap<>();
            data.put("error", "Query send error");
            data.put("reason", e.getLocalizedMessage());

            ObjectMapper objectMapper = new ObjectMapper();
            String stringData;
            try {
                stringData = objectMapper.writeValueAsString(data);
            } catch (JsonProcessingException jsonProcessingException) {
                stringData = e.getLocalizedMessage();
            }

            responseEntity = new ResponseEntity<>(stringData, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
}

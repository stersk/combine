package ua.com.tracktor.combine.controller;

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
import ua.com.tracktor.combine.service.QueryService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
public class QueryController {
    @Autowired
    Environment environment;

    @Autowired
    QueryService queryService;

    @PostMapping(path="/viber/{account}")
    public ResponseEntity<String> processRequest(@RequestBody String body, @RequestHeader HttpHeaders headers, HttpServletRequest request, @PathVariable String account) throws URISyntaxException {
        ResponseEntity<String> responseEntity;

        ObjectMapper mapper = new ObjectMapper();
        boolean proxyOnly = true;

        try {
            JsonNode responseBodyNode = mapper.readTree(body);
            JsonNode eventNode = responseBodyNode.get("event");

            switch (eventNode.asText()) {
                case "delivered":
                case "seen":
                case "failed":
                    proxyOnly = false;
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
            String path = basePath + request.getRequestURI().substring(7);

            URI uri = new URI(scheme, null, server, port, path, null, null);

            HttpEntity<String> httpEntity = new HttpEntity<>(body, headers);

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters()
                        .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // for correct cyrillic symbols in string body
                responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);
            } catch (HttpClientErrorException | HttpServerErrorException e) {
                String responseBody = e.getResponseBodyAsString();
                responseEntity = new ResponseEntity<>(responseBody, Objects.requireNonNull(HttpStatus.resolve(e.getRawStatusCode())));
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
            queryService.saveQuery(signature, account, body);
            queryService.runDelayedQueryProcessing();
            responseEntity = new ResponseEntity<>("", HttpStatus.OK);
        }

        return responseEntity;
    }
}

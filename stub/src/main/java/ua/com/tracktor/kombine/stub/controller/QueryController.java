package ua.com.tracktor.kombine.stub.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RestController
public class QueryController {
    @Autowired
    Environment environment;

    @PostMapping(path="/viber/{account}")
    public ResponseEntity<String> processRequest(@RequestBody String body, @RequestHeader HttpHeaders headers, HttpServletRequest request, @PathVariable String account) throws URISyntaxException {
        ResponseEntity<String> responseEntity;

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
            responseEntity = new ResponseEntity<>(responseBody, e.getStatusCode());
        } catch (Exception e) {
            StringBuilder sb = new StringBuilder();
            sb.append("{\"error\":\"Query send error\",")
            .append("\"reason\":\"").append(e.getLocalizedMessage()).append("\"}");

            responseEntity = new ResponseEntity<>(sb.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return responseEntity;
    }
}

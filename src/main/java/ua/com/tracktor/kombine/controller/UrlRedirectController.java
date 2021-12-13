package ua.com.tracktor.kombine.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import ua.com.tracktor.kombine.entity.User;
import ua.com.tracktor.kombine.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
public class UrlRedirectController {
    @Autowired
    UserService userService;

    @Autowired
    Environment environment;

    @GetMapping(path="/redirect/{account}/{source}/{userId}/{id}")
    public ResponseEntity<String> redirectToUrl(HttpServletRequest requestEntity, @PathVariable String account, @PathVariable String userId, @PathVariable String source, @PathVariable String id) throws URISyntaxException {
        Optional<User> user = userService.getUserDataByAccountId(account);
        if (user.isPresent()) {
            String scheme = environment.getProperty("viber-service.server.scheme");
            String server = environment.getProperty("viber-service.server.address");
            String basePath = environment.getProperty("viber-service.server.path");
            int port = Integer.parseInt(Objects.requireNonNull(environment.getProperty("viber-service.server.port")));

            String path = basePath.substring(0, basePath.length() - 6) + "redirectToUrl";
            URI uri = new URI(scheme, null, server, port, path, null, null);

            HttpHeaders authorizationHeaders = new HttpHeaders();
            userService.addBasicAuthHeader(account, authorizationHeaders);

            Map<String, String> body = new HashMap<>();
            body.put("account", account);
            body.put("userId",  userId);
            body.put("source",  source);
            body.put("id",      id);

            HttpEntity<Map<String, String>> httpEntity = new HttpEntity<>(body, authorizationHeaders);

            try {
                RestTemplate restTemplate = new RestTemplate();
                restTemplate.getMessageConverters()
                        .add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8)); // for correct cyrillic symbols in string body
                ResponseEntity<String> responseEntity = restTemplate.exchange(uri, HttpMethod.POST, httpEntity, String.class);

                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set("Location", responseEntity.getBody());

                return ResponseEntity.status(HttpStatus.FOUND).headers(responseHeaders).body(null);
            } catch (HttpClientErrorException e) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Page not found");
            } catch (HttpServerErrorException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Internal server error");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Page not found");
        }
    }
}

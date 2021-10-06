package ua.com.tracktor.kombine.service;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import ua.com.tracktor.kombine.data.UserRepository;
import ua.com.tracktor.kombine.entity.User;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public void addBasicAuthHeader(String account, MultiValueMap<String, String> headers) {
        Optional<User> userData = userRepository.findByAccountId(account);
        if (userData.isPresent()) {
            User user = userData.get();
            String auth = user.getLogin() + ":" + user.getPassword();
            byte[] encodedAuth = Base64.encodeBase64(
                    auth.getBytes(StandardCharsets.US_ASCII) );
            String authHeader = "Basic " + new String(encodedAuth);

            headers.add("Authorization", authHeader);
        }
    }

    public Optional<User> getUserDataByAccountId(String account) {
        return userRepository.findByAccountId(account);
    }
}

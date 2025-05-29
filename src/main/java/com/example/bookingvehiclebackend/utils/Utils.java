package com.example.bookingvehiclebackend.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Utils {
    public static Map<String, String> clientSecretHeader(String clientId, String clientSecret, String username, String password) {
        Map<String, String> headers = clientSecretHeader(clientId, clientSecret);
        if (Objects.nonNull(username)) {
            headers.put("username", username);
        }
        if (Objects.nonNull(password)) {
            headers.put("password", password);
        }

        return headers;
    }
    public static Map<String, String> clientSecretHeader(String clientId, String clientSecret) {
        Map<String, String> headers = new HashMap<>();
        headers.put("client_id", clientId);
        headers.put("client_secret", clientSecret);
        return headers;
    }
}

package com.example.bookingvehiclebackend.demo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.Map;

@Service
public class chatbotServiceDemo {

    private Map<String, String> responseMap;

    public void loadResponses() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        responseMap = mapper.readValue(
                new ClassPathResource("chatbot/responses.json").getInputStream(),
                new TypeReference<>() {}
        );
    }

    public String getResponse(String userInput) {
        String lowerInput = userInput.toLowerCase();
        for (String keyword : responseMap.keySet()) {
            if (lowerInput.contains(keyword)) {
                return responseMap.get(keyword);
            }
        }
        return "Xin lỗi, tôi chưa hiểu. Bạn có thể nói rõ hơn không?";
    }
}
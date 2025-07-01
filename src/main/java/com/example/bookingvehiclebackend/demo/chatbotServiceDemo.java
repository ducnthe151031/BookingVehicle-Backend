package com.example.bookingvehiclebackend.demo;

import com.example.bookingvehiclebackend.demo.chatReponseDemo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;


import java.io.InputStream;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class chatbotServiceDemo {

    private Map<String, chatReponseDemo> responseMap = new LinkedHashMap<>();


    public void init() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        InputStream input = new ClassPathResource("chatbot/responses.json").getInputStream();
        responseMap = mapper.readValue(input, new TypeReference<>() {});
    }

    public chatReponseDemo getResponse(String userInput) {
        String normalizedInput = removeVietnameseDiacritics(userInput.toLowerCase());

        for (Map.Entry<String, chatReponseDemo> entry : responseMap.entrySet()) {
            if (normalizedInput.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return new chatReponseDemo("Xin lỗi, tôi chưa hiểu. Bạn có thể nói rõ hơn không?", null);
    }

    private String removeVietnameseDiacritics(String input) {
        String temp = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(temp).replaceAll("").replaceAll("đ", "d").replaceAll("Đ", "D");
    }
}
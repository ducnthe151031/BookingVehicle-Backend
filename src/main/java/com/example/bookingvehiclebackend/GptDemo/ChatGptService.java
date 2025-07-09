package com.example.bookingvehiclebackend.GptDemo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ChatGptService {

    @Value("${openai.api.key}")
    private String apiKey;

    public String askChatGPT(String prompt) throws IOException {
        String apiUrl = "https://api.openai.com/v1/chat/completions";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(apiUrl);
            post.setHeader("Authorization", "Bearer " + apiKey);
            post.setHeader("Content-Type", "application/json");

            // JSON gửi lên API
            String jsonBody = """
                    {
                      "model": "gpt-3.5-turbo",
                      "messages": [
                        {"role": "user", "content": "%s"}
                      ]
                    }
                    """.formatted(prompt);

            post.setEntity(new StringEntity(jsonBody));

            var response = httpClient.execute(post);
            var responseBody = new String(response.getEntity().getContent().readAllBytes());

            // Parse JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);
            return root.path("choices").get(0).path("message").path("content").asText();
        }
    }
}

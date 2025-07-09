package com.example.bookingvehiclebackend.GptDemo;

import com.example.bookingvehiclebackend.GptDemo.ChatRequest;
import com.example.bookingvehiclebackend.GptDemo.ChatResponse;
import com.example.bookingvehiclebackend.GptDemo.ChatGptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class ChatController {

    @Autowired
    private ChatGptService chatGptService;

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        try {
            String reply = chatGptService.askChatGPT(request.getMessage());
            return new ChatResponse(reply);
        } catch (Exception e) {
            return new ChatResponse("Lỗi: " + e.getMessage());
        }
    }
}

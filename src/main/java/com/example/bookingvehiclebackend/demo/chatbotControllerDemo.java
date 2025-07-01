package com.example.bookingvehiclebackend.demo;

import com.example.bookingvehiclebackend.demo.chatRequestDemo;
import com.example.bookingvehiclebackend.demo.chatReponseDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin
public class chatbotControllerDemo {

    @Autowired
    private chatbotServiceDemo chatService;

    @PostMapping
    public chatReponseDemo chat(@RequestBody chatRequestDemo request) {
        return chatService.getResponse(request.getMessage());
    }
}
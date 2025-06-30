package com.example.bookingvehiclebackend.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class chatbotControllerDemo {

    @Autowired
    private chatbotServiceDemo chatbotService;

    @PostMapping
    public chatRequestDemo chat(@RequestBody chatRequestDemo request) {
        String reply = chatbotService.getReply(request.getMessage());
        return new chatRequestDemo(reply);
    }
}

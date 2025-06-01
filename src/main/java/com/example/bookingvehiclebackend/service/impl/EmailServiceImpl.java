package com.example.bookingvehiclebackend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

public class EmailServiceImpl {
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(String to, String subject, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
    }
}
package com.example.bookingvehiclebackend.service;

public interface EmailService {
    void sendEmail(String to, String subject, String content);
}

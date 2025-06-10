package com.example.bookingvehiclebackend.demo.controller;

import com.example.bookingvehiclebackend.demo.service.PasswordServiceDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PasswordControllerDemo {
    @Autowired
    private PasswordServiceDemo passwordService;

    @PostMapping("/forgot-password-demo")
    public ResponseEntity<?> forgotPassword(@RequestParam String email) {
        passwordService.generateResetToken(email);
        return ResponseEntity.ok("Link reset đã được gửi!");
    }

    @PostMapping("/reset-password-demo")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        passwordService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Mật khẩu đã được cập nhật.");
    }
}
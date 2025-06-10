package com.example.bookingvehiclebackend.demo.service;

import com.example.bookingvehiclebackend.demo.Entity.UserDemo;
import com.example.bookingvehiclebackend.demo.repository.UserRepositoryDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordServiceDemo {
    @Autowired
    private UserRepositoryDemo userRepository;

    public void generateResetToken(String email) {
        UserDemo user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setResetToken(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);

        // Gửi email (demo)
        System.out.println("Link reset: http://localhost:8080/reset-password-demo?token=" + token);
    }

    public void resetPassword(String token, String newPassword) {
        UserDemo user = userRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token không hợp lệ"));

        if (user.getTokenExpiry().isBefore(Instant.from(LocalDateTime.now()))) {
            throw new RuntimeException("Token hết hạn");
        }

        user.setPassword(newPassword); // nên mã hóa
        user.setResetToken((LocalDateTime) null);
        user.setTokenExpiry(null);
        userRepository.save(user);
    }
}
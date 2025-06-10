package com.example.bookingvehiclebackend.demo.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
public class UserDemo{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    private String resetToken;
    private LocalDateTime tokenExpiry;

    public void setResetToken(String token) {
    }

    public Instant getTokenExpiry() {
        return null;
    }

    public void setPassword(String newPassword) {
    }

    public void setTokenExpiry(Object o) {
    }

    public void setResetToken(LocalDateTime localDateTime) {
    }

    // Getters, Setters
}

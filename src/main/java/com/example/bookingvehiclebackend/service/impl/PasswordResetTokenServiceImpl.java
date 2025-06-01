package com.example.bookingvehiclebackend.service.impl;

import com.example.bookingvehiclebackend.dto.User;
import com.example.bookingvehiclebackend.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

public class PasswordResetTokenServiceImpl {
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public void createToken(User user, String token) {
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setToken(token);
        resetToken.setUser(user);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(1));
        tokenRepository.save(resetToken);
    }

    @Override
    public PasswordResetToken getByToken(String token) {
        return tokenRepository.findByToken(token);
    }

    @Override
    public void deleteToken(PasswordResetToken token) {
        tokenRepository.delete(token);
    }
}
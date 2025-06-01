package com.example.bookingvehiclebackend.service;

import com.example.bookingvehiclebackend.dto.Token;
import com.example.bookingvehiclebackend.dto.User;

public interface PasswordResetTokenService {
    void createToken(User user, String token);
    Token getByToken(String token);
    void deleteToken(Token token);
}

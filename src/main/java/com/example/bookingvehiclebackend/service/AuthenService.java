package com.example.bookingvehiclebackend.service;

import com.example.bookingvehiclebackend.dto.User;
import com.example.bookingvehiclebackend.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenService {
    LoginResponse login(AuthenRequest request);
    void saveUserToken(User user, String jwtToken);

    LoginResponse register(AuthenRequest request, HttpServletRequest httpServletRequest);
}

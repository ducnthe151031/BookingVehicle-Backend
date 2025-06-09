package com.example.bookingvehiclebackend.v1.service;

import com.example.bookingvehiclebackend.v1.dto.User;
import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenService {
    LoginResponse login(AuthenRequest request);
    void saveUserToken(User user, String jwtToken);

    LoginResponse register(AuthenRequest request, HttpServletRequest httpServletRequest);

    void changePassword(AuthenRequest request);

    LoginResponse forgotPassword(AuthenRequest request, HttpServletRequest httpServletRequest);

    String verifyEmail(String token);

}

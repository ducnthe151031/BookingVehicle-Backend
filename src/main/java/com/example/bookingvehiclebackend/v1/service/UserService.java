package com.example.bookingvehiclebackend.v1.service;

import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.request.BookingVehicleRequest;
import com.example.bookingvehiclebackend.v1.dto.request.ProfileRequest;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface UserService {
    Object bookingVehicle(BookingVehicleRequest request) throws Exception;
    Object profile();
    void changePassword(AuthenRequest request);

    LoginResponse forgotPassword(AuthenRequest request, HttpServletRequest httpServletRequest);

    String verifyEmail(String token);

    Object updateProfile(ProfileRequest profileRequest) throws IOException;

    void resetPassword(String token, HttpServletResponse response, AuthenRequest changePasswordRequest);
}


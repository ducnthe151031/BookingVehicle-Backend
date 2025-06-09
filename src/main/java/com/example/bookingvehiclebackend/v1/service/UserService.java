package com.example.bookingvehiclebackend.v1.service;

import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.request.BookingVehicleRequest;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface UserService {
    Object bookingVehicle(BookingVehicleRequest request);
    Object profile();
    void changePassword(AuthenRequest request);

    LoginResponse forgotPassword(AuthenRequest request, HttpServletRequest httpServletRequest);

    String verifyEmail(String token);
}

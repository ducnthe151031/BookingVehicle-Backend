package com.example.package com.example.bookingvehiclebackend.v1.service;

import com.example.bookingvehiclebackend.v1.dto.User;
import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.request.ProfileRequest;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenService {
    LoginResponse login(AuthenRequest request);

    void saveUserToken(User user, String jwtToken);

    LoginResponse register(AuthenRequest request, HttpServletRequest httpServletRequest);


}
.v1.service;

import com.example.bookingvehiclebackend.v1.dto.User;
import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.request.ProfileRequest;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthenService {
    LoginResponse login(AuthenRequest request);

    void saveUserToken(User user, String jwtToken);

    LoginResponse register(AuthenRequest request, HttpServletRequest httpServletRequest);


}

package com.example.bookingvehiclebackend.v1.controller;

import com.example.bookingvehiclebackend.utils.BaseApiResponse;
import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.request.ProfileRequest;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import com.example.bookingvehiclebackend.v1.service.AuthenService;
import com.example.bookingvehiclebackend.v1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "v1/auth", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthenController {
    private final AuthenService authenService;
    private final UserService userService;

    @PostMapping("/login")
    public BaseApiResponse<LoginResponse> login(@RequestBody AuthenRequest request) {
        return BaseApiResponse.succeed(authenService.login(request));
    }

    @PostMapping("/register")
    public BaseApiResponse<LoginResponse> register(@RequestBody AuthenRequest request, final HttpServletRequest httpServletRequest) {
        return BaseApiResponse.succeed(authenService.register(request, httpServletRequest));
    }
    @PostMapping("/forgot-password")
    public BaseApiResponse<LoginResponse> forgotPassword(@RequestBody AuthenRequest request, final HttpServletRequest httpServletRequest) {
        return BaseApiResponse.succeed(userService.forgotPassword(request,httpServletRequest));
    }
}

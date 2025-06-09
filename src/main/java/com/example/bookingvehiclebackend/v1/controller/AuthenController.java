package com.example.bookingvehiclebackend.v1.controller;

import com.example.bookingvehiclebackend.utils.BaseApiResponse;
import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import com.example.bookingvehiclebackend.v1.service.AuthenService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "v1/auth", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AuthenController {
    private final AuthenService authenService;

    @PostMapping("/login")
    public BaseApiResponse<LoginResponse> login(@RequestBody AuthenRequest request) {
        return BaseApiResponse.succeed(authenService.login(request));
    }

    @PostMapping("/register")
    public BaseApiResponse<LoginResponse> register(@RequestBody AuthenRequest request, final HttpServletRequest httpServletRequest) {
        return BaseApiResponse.succeed(authenService.register(request, httpServletRequest));
    }

    // TODO forgot password
    // TODO CHANGE-PASSWORD
    @GetMapping("/profile")
    public BaseApiResponse<?> profile() {
        return BaseApiResponse.succeed(authenService.profile());
    }

    @PostMapping("/change-password")
    public BaseApiResponse<Void> changePassword(@RequestBody AuthenRequest request, final HttpServletRequest httpServletRequest) {
        authenService.changePassword(request);
        return BaseApiResponse.succeed();
    }
}

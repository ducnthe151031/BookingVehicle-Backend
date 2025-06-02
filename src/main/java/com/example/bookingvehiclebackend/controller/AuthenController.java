package com.example.bookingvehiclebackend.controller;

import com.example.bookingvehiclebackend.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.dto.response.LoginResponse;
import com.example.bookingvehiclebackend.service.AuthenService;
import com.example.bookingvehiclebackend.utils.BaseApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "v1/auth", produces = APPLICATION_JSON_VALUE)
public class AuthenController {
    @Autowired
    private AuthenService authenService;


    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody AuthenRequest request) {
      return ResponseEntity.ok(authenService.login(request));
    }
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody AuthenRequest request, final HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(authenService.register(request, httpServletRequest));
    }
}

package com.example.bookingvehiclebackend.v1.controller;

import com.example.bookingvehiclebackend.utils.BaseApiResponse;
import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.request.BookingVehicleRequest;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import com.example.bookingvehiclebackend.v1.service.AuthenService;
import com.example.bookingvehiclebackend.v1.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "v1/user", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final AuthenService authenService;

    @PostMapping("/bookings")
    public BaseApiResponse<?> bookingVehicle(@RequestBody BookingVehicleRequest request) {
        return BaseApiResponse.succeed(userService.bookingVehicle(request));
    }
    @GetMapping("/profile")
    public BaseApiResponse<?> profile() {
        return BaseApiResponse.succeed(userService.profile());
    }
    @PostMapping("/change-password")
    public BaseApiResponse<Void> changePassword(@RequestBody AuthenRequest request) {
        userService.changePassword(request);
        return BaseApiResponse.succeed();
    }

    @PostMapping("/forgot-password")
    public BaseApiResponse<LoginResponse> forgotPassword(@RequestBody AuthenRequest request, final HttpServletRequest httpServletRequest) {
        return BaseApiResponse.succeed(userService.forgotPassword(request,httpServletRequest));
    }
    @GetMapping("/verify-email")
    public BaseApiResponse<Void> verifyEmail(@RequestParam String token,
                                             HttpServletResponse response) throws IOException {
        String successful = userService.verifyEmail(token);
        if ("Successful".equals(successful)) {
            response.sendRedirect("/verification-success.html");
        } else {
            response.sendRedirect("/verification-failed.html");
        }

        return BaseApiResponse.succeed();
    }

}

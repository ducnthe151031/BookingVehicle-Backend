package com.example.bookingvehiclebackend.v1.controller;

import com.example.bookingvehiclebackend.v1.dto.request.BookingVehicleRequest;
import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;
import com.example.bookingvehiclebackend.v1.service.UserService;
import com.example.bookingvehiclebackend.utils.BaseApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "v1/user", produces = APPLICATION_JSON_VALUE)
public class UserController {
    @Autowired
    private UserService userService;
    @PostMapping("/bookings")
    public BaseApiResponse<?> bookingVehicle(@RequestBody BookingVehicleRequest request){
        return BaseApiResponse.succeed(userService.rentalVehicle(request));
    }
}

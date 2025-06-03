package com.example.bookingvehiclebackend.v1.controller;

import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;
import com.example.bookingvehiclebackend.v1.service.AdminService;
import com.example.bookingvehiclebackend.utils.BaseApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "v1/admin", produces = APPLICATION_JSON_VALUE)
public class AdminController {
    @Autowired
    private AdminService adminService;

    @PostMapping("/cars")
    public BaseApiResponse<?> createVehicle(@RequestBody CreateVehicleRequest request){
        return BaseApiResponse.succeed(adminService.createVehicle(request));
    }
}

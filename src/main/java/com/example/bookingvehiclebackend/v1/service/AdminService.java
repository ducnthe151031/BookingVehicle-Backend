package com.example.bookingvehiclebackend.v1.service;

import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;

public interface AdminService {
    Object createVehicle(CreateVehicleRequest request);
}


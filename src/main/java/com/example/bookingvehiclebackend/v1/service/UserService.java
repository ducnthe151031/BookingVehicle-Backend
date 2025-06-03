package com.example.bookingvehiclebackend.v1.service;

import com.example.bookingvehiclebackend.v1.dto.request.BookingVehicleRequest;

public interface UserService {
    Object bookingVehicle(BookingVehicleRequest request);
}

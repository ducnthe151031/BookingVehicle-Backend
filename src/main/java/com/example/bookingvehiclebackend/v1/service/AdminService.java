package com.example.bookingvehiclebackend.v1.service;

import com.example.bookingvehiclebackend.v1.dto.Brand;
import com.example.bookingvehiclebackend.v1.dto.Category;
import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;

import java.util.List;

public interface AdminService {
    Object createVehicle(CreateVehicleRequest request);

    List<Category> categoryList();
    List<Brand> brandList();
}

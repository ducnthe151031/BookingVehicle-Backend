package com.example.bookingvehiclebackend.v1.service;

import com.example.bookingvehiclebackend.v1.dto.Brand;
import com.example.bookingvehiclebackend.v1.dto.Category;
import com.example.bookingvehiclebackend.v1.dto.VehicleType;
import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {
    Object createVehicle(CreateVehicleRequest request) throws IOException;

    List<Category> categoryList();
    List<Brand> brandList();

    Object approveBooking(String id);

    Object searchVehicles(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status);

    Object viewVehicle(String id);

    Object updateVehicle(CreateVehicleRequest request) throws IOException;

    void deleteVehicle(CreateVehicleRequest request);

    Object rentalList(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status);

    List<VehicleType> vehicleTypeList();

    Object rejectBooking(String id);
}

package com.example.bookingvehiclebackend.v1.service;

import com.example.bookingvehiclebackend.v1.dto.Brand;
import com.example.bookingvehiclebackend.v1.dto.Category;
import com.example.bookingvehiclebackend.v1.dto.User;
import com.example.bookingvehiclebackend.v1.dto.VehicleType;
import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface AdminService {
    Object createVehicle(CreateVehicleRequest request) throws IOException;

    List<Category> categoryList();
    List<Brand> brandList();

    Object approveBooking(String id);

    Object searchVehicles(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status,String fuelType);

    Object viewVehicle(String id);

    Object updateVehicle(CreateVehicleRequest request) throws IOException;

    void deleteVehicle(CreateVehicleRequest request);

    Object rentalList(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status,String fuelType);

    List<VehicleType> vehicleTypeList();

    Object rejectBooking(String id);

    void approveVehicle(CreateVehicleRequest request);

    Object searchVehiclesIsApproved(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status,String fuelType);

    Object createBrand(Brand brand);

    Object updateBrand(String id, Brand brand);

    void deleteBrand(String id);

    Object createCategory(Category category);

    Object updateCategory(String id, Category category);

    void deleteCategory(String id);

    List<User> getUserList();


    void deleteUser(String id);

    Object updateUserRole(String id, User user);

    void rejectVehicle(CreateVehicleRequest request);

    Object returnedBooking(String id);

    Object deliveredBooking(String id);

    Object getListUser();

    Object createUserList(AuthenRequest request, HttpServletRequest httpServletRequest);

    Object searchVehiclesByUser(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status, String fuelType);
}

package com.example.bookingvehiclebackend.v1.controller;

import com.example.bookingvehiclebackend.utils.BaseApiResponse;
import com.example.bookingvehiclebackend.v1.dto.Brand;
import com.example.bookingvehiclebackend.v1.dto.Category;
import com.example.bookingvehiclebackend.v1.dto.VehicleType;
import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;
import com.example.bookingvehiclebackend.v1.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "v1/admin", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    //test
    @PostMapping("/cars")
    public BaseApiResponse<?> createVehicle(@RequestBody CreateVehicleRequest request) {
        return BaseApiResponse.succeed(adminService.createVehicle(request));
    }

    @PutMapping("/approve-booking/{id}")
    public BaseApiResponse<?> approveBooking(@PathVariable String id) {
        return BaseApiResponse.succeed(adminService.approveBooking(id));
    }
    @PutMapping("/reject-booking/{id}")
    public BaseApiResponse<?> rejectBooking(@PathVariable String id) {
        return BaseApiResponse.succeed(adminService.rejectBooking(id));
    }

    @GetMapping("/category-list")
    public BaseApiResponse<List<Category>> getCategoryList() {
        return BaseApiResponse.succeed(adminService.categoryList());
    }
    @GetMapping("/vehicleType-list")
    public BaseApiResponse<List<VehicleType>> getVehicleTypeList() {
        return BaseApiResponse.succeed(adminService.vehicleTypeList());
    }


    @GetMapping("/list")
    public BaseApiResponse<?> getVehicleList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) String vehicleName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return BaseApiResponse.succeed(adminService.searchVehicles(brands, categories, vehicleName, startDate, endDate, pageable, status));
    }

    @GetMapping("/brand-list")
    public BaseApiResponse<List<Brand>> getBrandList() {
        return BaseApiResponse.succeed(adminService.brandList());
    }

    @PutMapping("/view")
    public BaseApiResponse<?> viewVehicle(@RequestBody CreateVehicleRequest request) {
        return BaseApiResponse.succeed(adminService.viewVehicle(request.getId()));
    }

    @PutMapping("/cars")
    public BaseApiResponse<?> updateVehicle(@RequestBody CreateVehicleRequest request) {
        return BaseApiResponse.succeed(adminService.updateVehicle(request));
    }
    @DeleteMapping("/cars")
    public BaseApiResponse<Void> deleteVehicle(@RequestBody CreateVehicleRequest request) {
        adminService.deleteVehicle(request);
        return BaseApiResponse.succeed();
    }
    @GetMapping("/rental-list")
    public BaseApiResponse<?> getRentalList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) String vehicleName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return BaseApiResponse.succeed(adminService.rentalList(brands, categories, vehicleName, startDate, endDate, pageable, status));
    }
}

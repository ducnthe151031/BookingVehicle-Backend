package com.example.bookingvehiclebackend.v1.controller;

import com.example.bookingvehiclebackend.utils.BaseApiResponse;
import com.example.bookingvehiclebackend.v1.dto.*;
import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.request.CouponRequest;
import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;
import com.example.bookingvehiclebackend.v1.exception.PvrsClientException;
import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import com.example.bookingvehiclebackend.v1.repository.VehicleRepository;
import com.example.bookingvehiclebackend.v1.service.AdminService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "v1/admin", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;
    private final VehicleRepository vehicleRepository;


    //test
    @PostMapping("/cars")
    public BaseApiResponse<?> createVehicle(@RequestBody CreateVehicleRequest request) throws IOException {
        return BaseApiResponse.succeed(adminService.createVehicle(request));
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path path = Paths.get("/uploads/images/" + filename);
            Resource resource = new UrlResource(path.toUri());

            if (resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/vehicles/{id}/images")
    public ResponseEntity<List<String>> getVehicleImages(@PathVariable String id) {
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new PvrsClientException(PvrsErrorHandler.VEHICLE_NOT_FOUND));

        if (vehicle.getImageUrl() == null || vehicle.getImageUrl().isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<String> imageUrls = Arrays.stream(vehicle.getImageUrl().split(","))
                .map(url -> url.startsWith("http") ? url : "/uploads/images/" + url)
                .collect(Collectors.toList());

        return ResponseEntity.ok(imageUrls);
    }

    @PutMapping("/approve-booking/{id}")
    public BaseApiResponse<?> approveBooking(@PathVariable String id) {
        return BaseApiResponse.succeed(adminService.approveBooking(id));
    }

    @PutMapping("/approve-delivered-booking/{id}")
    public BaseApiResponse<?> deliveredBooking(@PathVariable String id) {
        return BaseApiResponse.succeed(adminService.deliveredBooking(id));
    }

    @PutMapping("/approve-returned-booking/{id}")
    public BaseApiResponse<?> returnedBooking(@PathVariable String id) {
        return BaseApiResponse.succeed(adminService.returnedBooking(id));
    }
    @PutMapping("/reject-booking/{id}")
    public BaseApiResponse<?> rejectBooking(@PathVariable String id) {
        return BaseApiResponse.succeed(adminService.rejectBooking(id));
    }

    @GetMapping("/user-list")
    public BaseApiResponse<List<User>> getUserList() {
        return BaseApiResponse.succeed(adminService.getUserList());
    }

    @PostMapping("/user-list")
    public BaseApiResponse<?> createUserList(@RequestBody AuthenRequest request,final HttpServletRequest httpServletRequest) {
        return BaseApiResponse.succeed(adminService.createUserList(request,httpServletRequest));
    }

    @PutMapping("/user-list/{id}")
    public BaseApiResponse<?> updateUserRole(@PathVariable String id, @RequestBody User user) {
        return BaseApiResponse.succeed(adminService.updateUserRole(id,user));
    }

    @DeleteMapping("/user-list/{id}")
    public BaseApiResponse<?> deleteUser(@PathVariable String id) {
        adminService.deleteUser(id);
        return BaseApiResponse.succeed();
    }
    @GetMapping("/category-list")
    public BaseApiResponse<List<Category>> getCategoryList() {
        return BaseApiResponse.succeed(adminService.categoryList());
    }

    @PostMapping("/category-list")
    public BaseApiResponse<?> createCategory(@RequestBody Category category) {
        return BaseApiResponse.succeed(adminService.createCategory(category));
    }

    @PutMapping("/category-list/{id}")
    public BaseApiResponse<?> updateCategory(@PathVariable String id, @RequestBody Category category) {
        return BaseApiResponse.succeed(adminService.updateCategory(id,category));
    }

    @DeleteMapping("/category-list/{id}")
    public BaseApiResponse<?> deleteCategory(@PathVariable String id) {
        adminService.deleteCategory(id);
        return BaseApiResponse.succeed();
    }

    @GetMapping("/vehicleType-list")
    public BaseApiResponse<List<VehicleType>> getVehicleTypeList() {
        return BaseApiResponse.succeed(adminService.vehicleTypeList());
    }


    @GetMapping("/list")
    public BaseApiResponse<?> getVehicleList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) String vehicleName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fuelType

    ) {
        Pageable pageable = PageRequest.of(page, size);
        return BaseApiResponse.succeed(adminService.searchVehicles(brands, categories, vehicleName, startDate, endDate, pageable, status,fuelType));
    }

    @GetMapping("/listByUser")
    public BaseApiResponse<?> getVehicleListByUser(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) String vehicleName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fuelType


    ) {
        Pageable pageable = PageRequest.of(page, size);
        return BaseApiResponse.succeed(adminService.searchVehiclesByUser(brands, categories, vehicleName, startDate, endDate, pageable, status,fuelType));
    }

    @GetMapping("/list/approved")
    public BaseApiResponse<?> getVehicleListIsApproved(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> brands,
            @RequestParam(required = false) List<String> categories,
            @RequestParam(required = false) String vehicleName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fuelType

    ) {
        Pageable pageable = PageRequest.of(page, size);
        return BaseApiResponse.succeed(adminService.searchVehiclesIsApproved(brands, categories, vehicleName, startDate, endDate, pageable, status,fuelType));
    }

    @GetMapping("/brand-list")
    public BaseApiResponse<List<Brand>> getBrandList() {
        return BaseApiResponse.succeed(adminService.brandList());
    }

    @PostMapping("/brand-list")
    public BaseApiResponse<?> createBrand(@RequestBody Brand brand) {
        return BaseApiResponse.succeed(adminService.createBrand(brand));
    }

    @PutMapping("/brand-list/{id}")
    public BaseApiResponse<?> updateBrand(@PathVariable String id, @RequestBody Brand brand) {
        return BaseApiResponse.succeed(adminService.updateBrand(id,brand));
    }

    @DeleteMapping("/brand-list/{id}")
    public BaseApiResponse<?> deleteBrand(@PathVariable String id) {
        adminService.deleteBrand(id) ;
        return BaseApiResponse.succeed();
    }

    @PutMapping("/view")
    public BaseApiResponse<?> viewVehicle(@RequestBody CreateVehicleRequest request) {
        return BaseApiResponse.succeed(adminService.viewVehicle(request.getId()));
    }

    @PutMapping("/cars")
    public BaseApiResponse<?> updateVehicle(@RequestBody CreateVehicleRequest request) throws IOException {
        return BaseApiResponse.succeed(adminService.updateVehicle(request));
    }
    @DeleteMapping("/cars")
    public BaseApiResponse<Void> deleteVehicle(@RequestBody CreateVehicleRequest request) {
        adminService.deleteVehicle(request);
        return BaseApiResponse.succeed();
    }

    @DeleteMapping("/approve-vehicle")
    public BaseApiResponse<Void> approveVehicle(@RequestBody CreateVehicleRequest request) {
        adminService.approveVehicle(request);
        return BaseApiResponse.succeed();
    }

    @DeleteMapping("/reject-vehicle")
    public BaseApiResponse<Void> rejectVehicle(@RequestBody CreateVehicleRequest request) {
        adminService.rejectVehicle(request);
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
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String fuelType
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return BaseApiResponse.succeed(adminService.rentalList(brands, categories, vehicleName, startDate, endDate, pageable, status,fuelType));
    }
}

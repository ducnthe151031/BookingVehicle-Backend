package com.example.bookingvehiclebackend.v1.service.impl;

import com.example.bookingvehiclebackend.v1.dto.*;
import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;
import com.example.bookingvehiclebackend.v1.exception.NhgClientException;
import com.example.bookingvehiclebackend.v1.exception.NhgErrorHandler;
import com.example.bookingvehiclebackend.v1.repository.BrandRepository;
import com.example.bookingvehiclebackend.v1.repository.CategoryRepository;
import com.example.bookingvehiclebackend.v1.repository.UserRepository;
import com.example.bookingvehiclebackend.v1.repository.VehicleRepository;
import com.example.bookingvehiclebackend.v1.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    BrandRepository brandRepository;
    @Autowired
    VehicleRepository vehicleRepository;

    @Override
    public Object createVehicle(CreateVehicleRequest request) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails userDetails) {
                User user = (User) userDetails;
                if (Set.of(Role.ADMIN, Role.OWNER).contains(user.getRole())) {
                    Vehicle vehicle = new Vehicle();
                    vehicle.setVehicleName(request.getName());
                    vehicle.setBranchId(request.getBrand());
                    vehicle.setCategoryId(request.getCategory());
                    vehicle.setFuelType(request.getType());
                    vehicle.setSeatCount(request.getSeats());
                    vehicle.setPricePerDay(request.getDaily_price());
                    vehicle.setStatus(VehicleStatus.AVAILABLE.name());
                    vehicle.setOwnerId(Role.OWNER.equals(user.getRole()) ? user.getId() : null);
                    vehicle.setLiecensePlate(request.getLicensePlate());
                    vehicle.setCreatedBy(user.getUsername());
                    vehicle.setCreatedAt(Instant.now());
                    vehicle.setDescription(request.getDescription());
                    return vehicleRepository.save(vehicle);
                } else {
                    throw NhgClientException.ofHandler(NhgErrorHandler.NOT_ALLOW_TO_CREATE_VEHICLE);
                }

            }
        }
        return null;
    }

    @Override
    public List<Category> categoryList() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Brand> brandList() {
        return brandRepository.findAll();
    }
}

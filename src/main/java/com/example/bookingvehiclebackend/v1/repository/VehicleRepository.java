package com.example.bookingvehiclebackend.v1.repository;

import com.example.bookingvehiclebackend.v1.dto.Vehicle;
import com.example.bookingvehiclebackend.v1.dto.VehicleStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    // Tìm tất cả xe còn AVAILABLE
    List<Vehicle> findByStatus(VehicleStatus status);
}
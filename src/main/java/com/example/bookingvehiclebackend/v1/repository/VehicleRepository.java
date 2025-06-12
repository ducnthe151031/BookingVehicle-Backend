package com.example.bookingvehiclebackend.v1.repository;

import com.example.bookingvehiclebackend.v1.dto.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface VehicleRepository extends JpaRepository<Vehicle, String>, JpaSpecificationExecutor<Vehicle> {
    Optional<Vehicle> findByLiecensePlate(String liecensePlate);
}
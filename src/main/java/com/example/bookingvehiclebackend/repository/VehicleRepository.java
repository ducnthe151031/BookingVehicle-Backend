package com.example.bookingvehiclebackend.repository;

import com.example.bookingvehiclebackend.dto.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {
    List<Vehicle> findByAvailableTrue();
    List<Vehicle> findByOwnerId(String ownerId);
}
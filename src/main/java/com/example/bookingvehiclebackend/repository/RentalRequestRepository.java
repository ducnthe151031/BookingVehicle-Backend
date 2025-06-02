package com.example.bookingvehiclebackend.repository;

import com.example.bookingvehiclebackend.dto.RentalRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalRequestRepository extends JpaRepository<RentalRequest, String> {
}
package com.example.bookingvehiclebackend.repository;

import com.example.bookingvehiclebackend.dto.VehicleReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleReviewRepository extends JpaRepository<VehicleReview, String> {
}
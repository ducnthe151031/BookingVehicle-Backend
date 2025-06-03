package com.example.bookingvehiclebackend.v1.repository;

import com.example.bookingvehiclebackend.v1.dto.VehicleReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleReviewRepository extends JpaRepository<VehicleReview, String> {
}
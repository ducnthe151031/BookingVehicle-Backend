package com.example.bookingvehiclebackend.repository;

import com.example.bookingvehiclebackend.dto.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PromotionRepository extends JpaRepository<Promotion, String> {
}
package com.example.bookingvehiclebackend.repository;

import com.example.bookingvehiclebackend.dto.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, String> {
}
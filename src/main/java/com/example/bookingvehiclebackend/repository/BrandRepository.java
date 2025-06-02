package com.example.bookingvehiclebackend.repository;

import com.example.bookingvehiclebackend.dto.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrandRepository extends JpaRepository<Brand, String> {
}
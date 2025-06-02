package com.example.bookingvehiclebackend.repository;

import com.example.bookingvehiclebackend.dto.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, String> {
}
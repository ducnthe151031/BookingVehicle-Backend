package com.example.bookingvehiclebackend.repository;

import com.example.bookingvehiclebackend.dto.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, String> {
}
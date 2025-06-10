package com.example.bookingvehiclebackend.demo.repository;

import com.example.bookingvehiclebackend.demo.Entity.UserDemo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepositoryDemo extends JpaRepository<UserDemo, Long> {
    Optional<UserDemo> findByEmail(String email);
    Optional<UserDemo> findByResetToken(String token);
}
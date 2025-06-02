package com.example.bookingvehiclebackend.repository;

import com.example.bookingvehiclebackend.dto.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
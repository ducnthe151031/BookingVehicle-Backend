package com.example.bookingvehiclebackend.repository;

import com.example.bookingvehiclebackend.dto.UserTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTransactionRepository extends JpaRepository<UserTransaction, String> {
}
package com.example.bookingvehiclebackend.repository;

import com.example.bookingvehiclebackend.dto.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, String> {
}
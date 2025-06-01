package com.example.bookingvehiclebackend.repository;

import com.example.bookingvehiclebackend.dto.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, String> {
    List<Booking> findByUserId(String userId);
    List<Booking> findByStatus(String status);
}

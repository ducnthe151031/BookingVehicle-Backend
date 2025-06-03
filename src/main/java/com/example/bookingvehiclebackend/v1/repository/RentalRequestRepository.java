package com.example.bookingvehiclebackend.v1.repository;

import com.example.bookingvehiclebackend.v1.dto.RentalRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RentalRequestRepository extends JpaRepository<RentalRequest, String> {
    // Kiểm tra xem xe đã có booking nào chưa vào khoảng thời gian
    @Query("""
        SELECT rr 
        FROM RentalRequest rr 
        WHERE rr.vehicleId= :vehicleId 
          AND rr.status IN ('PENDING','APPROVED') 
          AND (
                (:start < rr.endDate AND :end > rr.startDate)
              )
        """)
    List<RentalRequest> findOverlappingRequests(
            @Param("vehicleId") Long vehicleId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);
}
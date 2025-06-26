package com.example.bookingvehiclebackend.v1.repository;

import com.example.bookingvehiclebackend.v1.dto.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {

    // Tìm review theo vehicleId, có phân trang
    Page<Review> findByVehicleId(String vehicleId, Pageable pageable);

    // Tìm tất cả review của một xe để tính toán rating
    List<Review> findByVehicleId(String vehicleId);

    // Kiểm tra xem một đơn thuê đã được review chưa
    boolean existsByBookingId(String bookingId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.vehicleId = :vehicleId")
    Double findAverageRatingByVehicleId(@Param("vehicleId") String vehicleId);
}
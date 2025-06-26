package com.example.bookingvehiclebackend.v1.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reviews", schema = "vehicle_rental_system")
public class Review {
    /**
     * Khóa chính của review, tự động tạo UUID.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 255)
    private String id;

    /**
     * ID của người dùng đã viết review.
     * Ánh xạ trực tiếp tới cột user_id trong CSDL.
     */
    @Column(name = "user_id", nullable = false, length = 255)
    private String userId;

    /**
     * ID của chiếc xe được review.
     */
    @Column(name = "vehicle_id",  length = 255)
    private String vehicleId;

    /**
     * ID của đơn thuê xe liên quan đến review này.
     * unique = true để đảm bảo một đơn thuê chỉ có một review.
     */
    @Column(name = "booking_id",  unique = true, length = 255)
    private String bookingId;

    /**
     * Điểm đánh giá (số sao), từ 1 đến 5.
     */
    @Column(name = "rating", nullable = false)
    private Integer rating;
    /**
     * Nội dung bình luận, có thể để trống.
     */
    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    /**
     * Thời gian tạo review, tự động được gán khi tạo mới.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Thời gian cập nhật review lần cuối, tự động được gán khi có cập nhật.
     */
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

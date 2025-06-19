package com.example.bookingvehiclebackend.v1.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "rental_requests", schema = "vehicle_rental_system")
public class RentalRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "customer_id", length = 100)
    private String customerId; // User có role = CUSTOMER

    @Column(name = "vehicle_id", length = 100)
    private String vehicleId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "status", length = 100)
    private String status; // PENDING, APPROVED, REJECTED, CANCELLED, COMPLETED

    @Column(name = "deposit_paid")
    private Boolean depositPaid;

    @Column(name = "total_price", precision = 10, scale = 2)
    private BigDecimal totalPrice; // tính = pricePerDay * số ngày

    @Column(name = "late_fee", precision = 10, scale = 2)
    private BigDecimal lateFee;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;
    @Column(name = "approved_by", length = 100)
    private String approved_by;
    @Column(name = "brand_id", length = 100)
    private String brandId;
    @Column(name = "category_id", length = 100)
    private String categoryId;

    @Column(name = "rent_type", length = 100)
    private String rentType;

    @Column(name = "url", length = 100)
    private String url;

    @Column(name = "order_code")
    private Long orderCode;

    @Column(name = "payment_status")
    private boolean paymentStatus;
}

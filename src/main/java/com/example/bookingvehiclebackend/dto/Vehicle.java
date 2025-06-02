package com.example.bookingvehiclebackend.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "vehicles", schema = "bookingcar")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "owner_id", length = 100)
    private String ownerId;

    @Column(name = "vehicle_name", length = 100)
    private String vehicleName;

    @Column(name = "branch_id", length = 100)
    private String branchId;

    @Column(name = "category_id", length = 100)
    private String categoryId; // Ví dụ: "SUV", "Sedan", "7 chỗ", ...

    @Column(name = "fuel_type", length = 100)
    private String fuelType; // Ví dụ: GASOLINE, DIESEL, ...

    @Column(name = "seat_count")
    private Integer seatCount;

    @Column(name = "liecense_plate", length = 100)
    private String liecensePlate;

    @Column(name = "price_per_day", precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "status", length = 100)
    private String status; // AVAILABLE, RENTED, MAINTENANCE, HIDDEN

    @Column(name = "image_url", length = 100)
    private String imageUrl;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

}
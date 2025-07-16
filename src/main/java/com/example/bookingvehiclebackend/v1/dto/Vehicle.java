package com.example.bookingvehiclebackend.v1.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "vehicles", schema = "vehicle_rental_system")
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
    private String status; // AVAILABLE, PENDING ,RENTED

    @Column(name = "image_url", length = 100)
    private String imageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "vehicle_type_id", length = 100)
    private String vehicleTypeId;

    @Column(name = "price_per_hour", length = 100)
    private BigDecimal pricePerHour;

    @Column(name = "gear_box", length = 100)
    private String gearBox;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "registration_document_url", length = 255) // Stores the URL of the registration document image
    private String registrationDocumentUrl;

    @Column(name = "approved") // Boolean flag for approval status
    private Boolean approved; // Default value (false) will be set by JPA if no other default is specified

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "reason", length = 100)
    private String reason;
}
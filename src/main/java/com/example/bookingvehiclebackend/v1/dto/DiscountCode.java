package com.example.bookingvehiclebackend.v1.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "discount_codes", schema = "car_rental_system")
public class DiscountCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "code", length = 100)
    private String code;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "expiry_date")
    private Instant expiryDate;

    @Column(name = "used_count")
    private Integer usedCount;

    @Column(name = "max_use")
    private Integer maxUse;

    @Column(name = "created_by", length = 100)
    private String createdBy;

}
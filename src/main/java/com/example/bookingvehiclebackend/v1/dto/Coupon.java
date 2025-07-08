package com.example.bookingvehiclebackend.v1.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "Coupons", schema = "vehicle_rental_system")
public class Coupon {

    /**
     * The unique identifier for the coupon.
     * Mapped to the PRIMARY KEY column 'Id'.
     * Using UUID generation strategy as shown in the Category example.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "Id", length = 255)
    private String id;

    /**
     * The unique code that customers use to apply the discount.
     * Mapped to the 'CouponCode' column, which is unique and cannot be null.
     */
    @Column(name = "coupon_code", length = 50, nullable = false, unique = true)
    private String couponCode;

    /**
     * The amount of the discount.
     * Using BigDecimal for precise currency representation.
     * Mapped to the 'DiscountAmount' column, which cannot be null.
     */
    @Column(name = "discount_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal discountAmount;
}

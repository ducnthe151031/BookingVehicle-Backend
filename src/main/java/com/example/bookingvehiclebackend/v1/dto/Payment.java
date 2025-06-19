package com.example.bookingvehiclebackend.v1.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Getter
@Setter
@Table(name = "payments", schema = "vehicle_rental_system")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "is_payment", nullable = true)
    private boolean isPayment;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "booking_id", nullable = false)
    private String bookingId;





}





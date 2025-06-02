package com.example.bookingvehiclebackend.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "user_transactions", schema = "car_rental_system")
public class UserTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "action", length = 100)
    private String action;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "created_at")
    private Instant createdAt;

}
package com.example.bookingvehiclebackend.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "notifications", schema = "car_rental_system")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "title", length = 100)
    private String title;

    @Lob
    @Column(name = "message")
    private String message;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "type", length = 100)
    private String type;

}
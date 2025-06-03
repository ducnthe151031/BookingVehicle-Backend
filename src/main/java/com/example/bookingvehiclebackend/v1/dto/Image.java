package com.example.bookingvehiclebackend.v1.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "images", schema = "car_rental_system")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", length = 100)
    private String id;

    @Column(name = "vehicle_id", length = 100)
    private String vehicleId;

    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "uploaded_at")
    private Instant uploadedAt;

}
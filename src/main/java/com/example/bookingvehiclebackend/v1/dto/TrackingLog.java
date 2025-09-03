package com.example.bookingvehiclebackend.v1.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tracking_log", schema = "bookingcar")
@Getter
@Setter
public class TrackingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "Vehicle_ID", nullable = false)
    private String vehicleId;

    @Column(name = "User_ID", nullable = false)
    private String userId;

    @Column(name = "Longitude",nullable = false)
    private Double longitude;

    @Column(name = "Latitude", nullable = false)
    private Double latitude;

    @Column(name = "Tracking_Time", nullable = false)
    private LocalDateTime trackingTime = LocalDateTime.now();

}


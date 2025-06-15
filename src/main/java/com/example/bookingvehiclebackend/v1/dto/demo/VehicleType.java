package com.example.bookingvehiclebackend.v1.dto.demo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


public class VehicleType {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "type", length = 45)
    private String type;
}
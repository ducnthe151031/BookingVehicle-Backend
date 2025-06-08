package com.example.bookingvehiclebackend.v1.dto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vehicle_type")
public class VehicleType {
    @Id
    @Column(name = "id")
    private Long id;

    @Column(name = "type", length = 45)
    private String type;
}
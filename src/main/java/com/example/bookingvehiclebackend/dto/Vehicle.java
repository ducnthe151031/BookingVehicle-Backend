package com.example.bookingvehiclebackend.dto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
@Entity
@Table(name = "vehicle", schema = "bookingcar")
public class Vehicle {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    private User owner;

    @Column(name = "brand", length = 100)
    private String brand;

    @Column(name = "model", length = 100)
    private String model;

    @Column(name = "license_plate", length = 20, unique = true)
    private String licensePlate;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "transmission", length = 50)
    private String transmission;

    @Column(name = "price_per_day", precision = 10, scale = 2)
    private BigDecimal pricePerDay;

    @Column(name = "available")
    private boolean available;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "image_url", length = 255)
    @JsonProperty("imageUrl")
    private String imageUrl;

}

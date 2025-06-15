package com.example.bookingvehiclebackend.v1.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingVehicleRequest {
    private String vehicleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private boolean depositPaid;
    private String description;
    private String licensePlate;
    private String brandId;
    private String categoryId;
    private String rentType;
}

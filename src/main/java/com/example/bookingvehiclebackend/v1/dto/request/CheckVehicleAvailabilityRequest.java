package com.example.bookingvehiclebackend.v1.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckVehicleAvailabilityRequest {
    private String vehicleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}

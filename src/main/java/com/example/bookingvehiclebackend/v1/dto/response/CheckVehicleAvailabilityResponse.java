package com.example.bookingvehiclebackend.v1.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CheckVehicleAvailabilityResponse {
    private boolean available;
    private String message;
    private List<ConflictBooking> conflictBookings;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ConflictBooking {
        private String bookingId;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String status;
    }
}

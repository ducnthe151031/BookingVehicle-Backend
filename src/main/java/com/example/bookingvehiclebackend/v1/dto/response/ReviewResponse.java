package com.example.bookingvehiclebackend.v1.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {
    private String id;
    private String userId;
    private String username;
    private String vehicleId;
    private String bookingId;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


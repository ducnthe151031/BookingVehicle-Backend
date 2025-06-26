package com.example.bookingvehiclebackend.v1.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateReviewRequest {
    private String vehicleId;
    private Integer rating;   // Số sao đánh giá (1-5)
    private String comment;
}

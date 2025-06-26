package com.example.bookingvehiclebackend.v1.dto.request;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter

public class UpdateReviewRequest {
    private Integer rating;   // Số sao mới
    private String comment;
}

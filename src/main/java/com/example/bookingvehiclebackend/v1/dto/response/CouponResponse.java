package com.example.bookingvehiclebackend.v1.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class CouponResponse {
    private String id;
    private String couponCode;
    private BigDecimal discountAmount;
}

package com.example.bookingvehiclebackend.v1.dto.request;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
@Getter
@Setter
public class CouponRequest {
    private String couponCode;
    private BigDecimal discountAmount;
}

package com.example.bookingvehiclebackend.v1.service;

import com.example.bookingvehiclebackend.v1.dto.request.CouponRequest;
import com.example.bookingvehiclebackend.v1.dto.response.CouponResponse;

import java.util.List;

public interface CouponService {
    CouponResponse createCoupon(CouponRequest request);
    List<CouponResponse> getAllCoupons();
    CouponResponse getCouponById(String id);
    CouponResponse updateCoupon(String id, CouponRequest request);
    void deleteCoupon(String id);
    CouponResponse validateAndGetCoupon(String couponCode);

}

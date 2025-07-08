package com.example.bookingvehiclebackend.v1.controller;

import com.example.bookingvehiclebackend.utils.BaseApiResponse;
import com.example.bookingvehiclebackend.v1.dto.request.CouponRequest;
import com.example.bookingvehiclebackend.v1.dto.response.CouponResponse;
import com.example.bookingvehiclebackend.v1.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.util.MimeTypeUtils.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "v1/coupons", produces = APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CouponController {

    private final CouponService couponService;

    @PostMapping
    public BaseApiResponse<CouponResponse> createCoupon(@RequestBody CouponRequest request) {
        return BaseApiResponse.succeed(couponService.createCoupon(request));
    }

    @GetMapping
    public BaseApiResponse<List<CouponResponse>> getAllCoupons() {
        return BaseApiResponse.succeed(couponService.getAllCoupons());
    }

    @GetMapping("/{id}")
    public BaseApiResponse<CouponResponse> getCouponById(@PathVariable String id) {
        return BaseApiResponse.succeed(couponService.getCouponById(id));
    }

    @PutMapping("/{id}")
    public BaseApiResponse<CouponResponse> updateCoupon(@PathVariable String id, @RequestBody CouponRequest request) {
        return BaseApiResponse.succeed(couponService.updateCoupon(id, request));
    }

    @DeleteMapping("/{id}")
    public BaseApiResponse<Void> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return BaseApiResponse.succeed();
    }


    @GetMapping("/code/{couponCode}")
    public BaseApiResponse<CouponResponse> getCouponByCode(@PathVariable String couponCode) {
        return BaseApiResponse.succeed(couponService.validateAndGetCoupon(couponCode));
    }
}

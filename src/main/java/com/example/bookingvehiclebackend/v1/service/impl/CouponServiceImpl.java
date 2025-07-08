package com.example.bookingvehiclebackend.v1.service.impl;

import com.example.bookingvehiclebackend.v1.dto.Coupon;
import com.example.bookingvehiclebackend.v1.dto.request.CouponRequest;
import com.example.bookingvehiclebackend.v1.dto.response.CouponResponse;
import com.example.bookingvehiclebackend.v1.exception.PvrsClientException;
import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import com.example.bookingvehiclebackend.v1.repository.CouponRepository;
import com.example.bookingvehiclebackend.v1.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public CouponResponse createCoupon(CouponRequest request) {
        if (couponRepository.existsByCouponCode(request.getCouponCode())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.COUPON_IS_EXISTED); // Giả sử bạn có lỗi này
        }

        Coupon coupon = new Coupon();
        coupon.setCouponCode(request.getCouponCode());
        coupon.setDiscountAmount(request.getDiscountAmount());

        Coupon savedCoupon = couponRepository.save(coupon);
        return mapToCouponResponse(savedCoupon);
    }

    @Override
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::mapToCouponResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CouponResponse getCouponById(String id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> PvrsClientException.ofHandler(PvrsErrorHandler.COUPON_NOT_FOUND)); // Giả sử bạn có lỗi này
        return mapToCouponResponse(coupon);
    }

    @Override
    public CouponResponse updateCoupon(String id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> PvrsClientException.ofHandler(PvrsErrorHandler.COUPON_NOT_FOUND));

        // Kiểm tra nếu mã coupon mới đã tồn tại và không phải là của chính coupon này
        couponRepository.findByCouponCode(request.getCouponCode()).ifPresent(existingCoupon -> {
            if (!existingCoupon.getId().equals(id)) {
                throw PvrsClientException.ofHandler(PvrsErrorHandler.COUPON_IS_EXISTED);
            }
        });

        coupon.setCouponCode(request.getCouponCode());
        coupon.setDiscountAmount(request.getDiscountAmount());

        Coupon updatedCoupon = couponRepository.save(coupon);
        return mapToCouponResponse(updatedCoupon);
    }

    @Override
    public void deleteCoupon(String id) {
        if (!couponRepository.existsById(id)) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.COUPON_NOT_FOUND);
        }
        couponRepository.deleteById(id);
    }



    @Override
    public CouponResponse validateAndGetCoupon(String couponCode) {
        Coupon coupon = couponRepository.findByCouponCode(couponCode)
                .orElseThrow(() -> PvrsClientException.ofHandler(PvrsErrorHandler.COUPON_NOT_FOUND));

        return mapToCouponResponse(coupon);
    }

    private CouponResponse mapToCouponResponse(Coupon coupon) {
        CouponResponse response = new CouponResponse();
        response.setId(coupon.getId());
        response.setCouponCode(coupon.getCouponCode());
        response.setDiscountAmount(coupon.getDiscountAmount());
        return response;
    }

}

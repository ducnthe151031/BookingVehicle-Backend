package com.example.bookingvehiclebackend.v1.repository;
import com.example.bookingvehiclebackend.v1.dto.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface CouponRepository extends JpaRepository<Coupon, String>{
    Optional<Coupon> findByCouponCode(String couponCode);
    boolean existsByCouponCode(String couponCode);
}

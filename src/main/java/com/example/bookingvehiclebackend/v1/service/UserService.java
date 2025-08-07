package com.example.bookingvehiclebackend.v1.service;

import com.example.bookingvehiclebackend.v1.dto.request.*;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public interface UserService {
    Object bookingVehicle(BookingVehicleRequest request) throws Exception;
    Object profile();
    void changePassword(AuthenRequest request);

    LoginResponse forgotPassword(AuthenRequest request, HttpServletRequest httpServletRequest);

    String verifyEmail(String token);

    Object updateProfile(ProfileRequest profileRequest) throws IOException;

    void resetPassword(String token, HttpServletResponse response, AuthenRequest changePasswordRequest);

    Object rentalList(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status,String fuelType);



    Object getReviewsByVehicleId(String vehicleId, Pageable pageable);

    Object updateReview(String reviewId, UpdateReviewRequest request);

    void deleteReview(String reviewId);

    Object createReview(CreateReviewRequest request);

    Double calculateAverageRating(String vehicleId);


    Object payLateFee(String id) throws Exception;

    Object payLateFee(String request) throws Exception;

}

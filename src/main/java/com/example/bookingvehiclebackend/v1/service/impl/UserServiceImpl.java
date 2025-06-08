package com.example.bookingvehiclebackend.v1.service.impl;

import com.example.bookingvehiclebackend.v1.dto.*;
import com.example.bookingvehiclebackend.v1.dto.request.BookingVehicleRequest;
import com.example.bookingvehiclebackend.v1.exception.PvrsClientException;
import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import com.example.bookingvehiclebackend.v1.repository.RentalRequestRepository;
import com.example.bookingvehiclebackend.v1.repository.VehicleRepository;
import com.example.bookingvehiclebackend.v1.service.UserService;
import com.example.bookingvehiclebackend.v1.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final VehicleRepository vehicleRepository;

    private final RentalRequestRepository rentalRequestRepository;

    @Override
    public Object bookingVehicle(BookingVehicleRequest request) {
        if (!SecurityUtils.hasRole(Role.USER)) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.NOT_ALLOW_TO_BOOK_VEHICLE);
        }

        User user = SecurityUtils.getCurrentUser().orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
        Vehicle v = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));
        if (!Objects.equals(v.getStatus(), VehicleStatus.AVAILABLE.name())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.VEHICLE_UNAVAILABLE);
        }

        List<RentalRequest> overlaps = rentalRequestRepository.findOverlappingRequests(request.getVehicleId(), request.getStartDate(), request.getEndDate());
        if (!overlaps.isEmpty()) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.VEHICLE_ALREADY_BOOKED);
        }

        RentalRequest rr = new RentalRequest();
        rr.setVehicleId(request.getVehicleId());
        rr.setCustomerId(user.getId());
        rr.setStartDate(request.getStartDate());
        rr.setEndDate(request.getEndDate());
        rr.setStatus(RentalStatus.PENDING.name());
        rr.setDepositPaid(request.isDepositPaid());
        rr.setCreatedAt(LocalDateTime.now());
        rr.setCreatedBy(user.getUsername());
        // Tính totalPrice = pricePerDay * số ngày (làm ví dụ đơn giản: 1 ngày = 24h)

        long daysBetween = ChronoUnit.DAYS.between(rr.getStartDate().toLocalDate(), rr.getEndDate().toLocalDate());
        if (daysBetween <= 0) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.RENTAL_TIME_OVER_0_DAY);
        }

        BigDecimal total = v.getPricePerDay().multiply(BigDecimal.valueOf(daysBetween));
        rr.setTotalPrice(total);
        return rentalRequestRepository.save(rr);
    }
}

package com.example.bookingvehiclebackend.v1.schedule;

import com.example.bookingvehiclebackend.v1.dto.RentalRequest;
import com.example.bookingvehiclebackend.v1.dto.Vehicle;
import com.example.bookingvehiclebackend.v1.exception.PvrsClientException;
import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import com.example.bookingvehiclebackend.v1.repository.RentalRequestRepository;
import com.example.bookingvehiclebackend.v1.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LateReturnCheckService {
    private final RentalRequestRepository rentalRequestRepository;
    private final VehicleRepository vehicleRepository;

    @Scheduled(fixedRate = 20000) // Chạy vào lúc 00:00 hàng ngày
    public void checkForLateReturns() {
        List<RentalRequest> activeRentals = rentalRequestRepository.findByEndDateBeforeAndReturnDateIsNull(LocalDateTime.now());
        System.out.println("Số lượng rental cần xử lý: " + activeRentals.size());

        for (RentalRequest rental : activeRentals) {
            if (!rental.isLate()) {
                System.out.println(">>> Đang test scheduler: " + LocalDateTime.now());
                rental.setLate(true);
                calculateLateFee(rental);

                rentalRequestRepository.save(rental);

                // Gửi thông báo cho người dùng
//                notificationService.sendLateReturnNotification(rental);
            }
        }
    }
    private void calculateLateFee(RentalRequest rental) {
        long hoursLate = ChronoUnit.HOURS.between(rental.getEndDate(), LocalDateTime.now());
        Vehicle vehicle = vehicleRepository.findById(rental.getVehicleId())
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));

        BigDecimal calculatedFee = vehicle.getPricePerHour().multiply(new BigDecimal(hoursLate));
        rental.setLateFee(calculatedFee);
    }
}

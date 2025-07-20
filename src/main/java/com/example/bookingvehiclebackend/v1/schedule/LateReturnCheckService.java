package com.example.bookingvehiclebackend.v1.schedule;

import com.example.bookingvehiclebackend.v1.dto.RentalRequest;
import com.example.bookingvehiclebackend.v1.repository.RentalRequestRepository;
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

    @Scheduled(cron = "0 0 0 * * ?") // Chạy vào lúc 00:00 hàng ngày
    public void checkForLateReturns() {
        List<RentalRequest> activeRentals = rentalRequestRepository.findByEndDateBeforeAndReturnDateIsNull(LocalDateTime.now());
        for (RentalRequest rental : activeRentals) {
            if (!rental.isLate()) {
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
        BigDecimal hourlyLateFee = new BigDecimal("50000"); // 50k VND mỗi giờ trễ

        BigDecimal calculatedFee = hourlyLateFee.multiply(new BigDecimal(hoursLate));
        rental.setLateFee(calculatedFee);
    }
}

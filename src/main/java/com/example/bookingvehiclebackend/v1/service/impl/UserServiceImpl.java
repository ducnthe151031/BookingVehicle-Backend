package com.example.bookingvehiclebackend.v1.service.impl;

import com.example.bookingvehiclebackend.config.JwtService;
import com.example.bookingvehiclebackend.v1.dto.*;
import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.request.BookingVehicleRequest;
import com.example.bookingvehiclebackend.v1.dto.request.ProfileRequest;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import com.example.bookingvehiclebackend.v1.event.PasswordResetEvent;
import com.example.bookingvehiclebackend.v1.event.RegistrationCompleteEvent;
import com.example.bookingvehiclebackend.v1.exception.PvrsClientException;
import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import com.example.bookingvehiclebackend.v1.repository.RentalRequestRepository;
import com.example.bookingvehiclebackend.v1.repository.TokenRepository;
import com.example.bookingvehiclebackend.v1.repository.UserRepository;
import com.example.bookingvehiclebackend.v1.repository.VehicleRepository;
import com.example.bookingvehiclebackend.v1.service.UserService;
import com.example.bookingvehiclebackend.v1.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final VehicleRepository vehicleRepository;

    private final RentalRequestRepository rentalRequestRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ApplicationEventPublisher publisher;
    private final TokenRepository tokenRepository;


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
    @Override
    public Object profile() {
        return SecurityUtils.getCurrentUser()
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
    }

    @Override
    public void changePassword(AuthenRequest request) {
        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.EMAIL_NOT_FOUND);
        }
        if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
        } else {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.INVALID_PASSWORD);
        }
    }

    @Override
    public LoginResponse forgotPassword(AuthenRequest request, HttpServletRequest httpServletRequest) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.EMAIL_NOT_FOUND));
        //Neu nhu dung email roi -> thu hoi token cua email hien tai
        revokeAllUserTokens(user);
        String newToken = jwtService.generateToken(user);
        String resetUrl = "http://localhost:5173/forgotPassword?token=";
        publisher.publishEvent(new PasswordResetEvent(user, resetUrl, newToken));
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(newToken);
        return loginResponse;
    }

    @Override
    public String verifyEmail(String token) {
        Token theToken = tokenRepository.findByAccessToken(token)
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.TOKEN_INVALID));
        if ("ACTIVE".equals(theToken.getUser().getFlagActive())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.USER_IS_VERIFIED);
        }
        if (jwtService.isTokenValid(theToken.getAccessToken(), theToken.getUser())
                && "INACTIVE".equals(theToken.getUser().getFlagActive())) {
            theToken.getUser().setFlagActive("ACTIVE");
            userRepository.save(theToken.getUser());
        }
        return "Successful";
    }

    @Override
    public Object updateProfile(ProfileRequest profileRequest) {
        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
        user.setEmail(profileRequest.getEmail());
        user.setUsername(profileRequest.getFirstName()+ " " + profileRequest.getLastName());
        user.setPhoneNumber(profileRequest.getPhoneNumber());
        user.setAddress(profileRequest.getAddress());
        return userRepository.save(user);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}

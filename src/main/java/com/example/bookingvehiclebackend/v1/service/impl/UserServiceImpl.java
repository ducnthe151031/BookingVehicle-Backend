package com.example.bookingvehiclebackend.v1.service.impl;

import com.example.bookingvehiclebackend.config.JwtService;
import com.example.bookingvehiclebackend.v1.dto.*;
import com.example.bookingvehiclebackend.v1.dto.request.*;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import com.example.bookingvehiclebackend.v1.event.PasswordResetEvent;
import com.example.bookingvehiclebackend.v1.exception.PvrsClientException;
import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import com.example.bookingvehiclebackend.v1.repository.*;
import com.example.bookingvehiclebackend.v1.service.UserService;
import com.example.bookingvehiclebackend.v1.utils.SecurityUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import vn.payos.PayOS;
import vn.payos.type.PaymentData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

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
    private final PaymentRepository paymentRepository;
    private final ReviewRepository reviewRepository;


    @Override
    public Object bookingVehicle(BookingVehicleRequest request) throws Exception {
        if (!SecurityUtils.hasRole(Role.USER)) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.NOT_ALLOW_TO_BOOK_VEHICLE);
        }
        User user = SecurityUtils.getCurrentUser().orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
        Vehicle v = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));
//        if (!Objects.equals(v.getStatus(), VehicleStatus.AVAILABLE.name())) {
//            throw PvrsClientException.ofHandler(PvrsErrorHandler.VEHICLE_UNAVAILABLE);
//        }
        List<RentalRequest> overlaps = rentalRequestRepository.findOverlappingRequests(request.getVehicleId(), request.getStartDate(), request.getEndDate());
        if (!overlaps.isEmpty()) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.VEHICLE_ALREADY_BOOKED);
        }
        RentalRequest rr = new RentalRequest();
        rr.setVehicleId(request.getVehicleId());
        rr.setCustomerId(user.getId());
        rr.setStartDate(request.getStartDate());
        rr.setEndDate(request.getEndDate());
        rr.setStatus(RentalStatus.AVAILABLE.name());
        rr.setDepositPaid(request.isDepositPaid());
        rr.setCreatedAt(LocalDateTime.now());
        rr.setCreatedBy(user.getUsername());
        rr.setBrandId(request.getBrandId());
        rr.setCategoryId(request.getCategoryId());
        rr.setRentType(request.getRentType());
        rr.setTotalPrice(request.getTotalPrice());
        rr.setPaymentStatus(false);
        long orderCode = System.currentTimeMillis();
        rr.setOrderCode(orderCode);
        int totalPrice = request.getTotalPrice().intValue();
        PayOS payOS = new PayOS("65f3da88-9fa3-4ef3-8a5d-9393a14d3b84","c0473543-c34c-4601-8f23-88fc7fec2645","f3d3ea900cf6dd94e57b86b3c09ae3946ab397569ea3a930559a1c2a0797f25f") ;
        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(totalPrice)
                .description("Đơn thuê xe của " + user.getUsername())
                .returnUrl("http://localhost:8080/v1/user/payment/success?orderCode=" + orderCode) // Nếu thanh toán thành công
                .cancelUrl("http://localhost:8080/v1/user/payment/failed?orderCode=" + orderCode) // Nếu hủy không thanh toán
                .build();
        rr.setUrl(payOS.createPaymentLink(paymentData).getCheckoutUrl());
        vehicleRepository.save(v);
        RentalRequest savedRentalRequest = rentalRequestRepository.save(rr);

        Payment payment = new Payment();
        payment.setUserId(user.getId());
        payment.setAmount(totalPrice);
        payment.setDescription("description");
        payment.setUrl(rr.getUrl());
        payment.setBookingId(savedRentalRequest.getId());
        paymentRepository.save(payment);
        return savedRentalRequest ;
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
            throw PvrsClientException.ofHandler(PvrsErrorHandler.NOT_YOUR_EMAIL);
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
        User user = userRepository.findByEmailAndUsername(request.getEmail(), request.getUsername())
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
    public Object updateProfile(ProfileRequest profileRequest) throws IOException {
        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
        user.setEmail(profileRequest.getEmail());
        user.setFullName(profileRequest.getFullName());
        user.setPhoneNumber(profileRequest.getPhoneNumber());
        user.setAddress(profileRequest.getAddress());



        if (profileRequest.getDriverLicenseUrl() != null && !profileRequest.getDriverLicenseUrl().isEmpty()) {
            try {
                byte[] driverLicenseUrl = Base64.getDecoder().decode(profileRequest.getDriverLicenseUrl());
                String driverLicenseUrlName = saveImageToFileSystem(driverLicenseUrl); // Lưu ảnh và lấy đường dẫn
                user.setDriverLicenseUrl(driverLicenseUrlName);

            } catch (IllegalArgumentException e)
            {
                user.setDriverLicenseUrl(profileRequest.getDriverLicenseUrl());

            }
        }


        if (profileRequest.getCitizenIdCardUrl() != null && !profileRequest.getCitizenIdCardUrl().isEmpty()) {
            try {
                byte[] citizenIdCardUrl = Base64.getDecoder().decode(profileRequest.getCitizenIdCardUrl());
                String citizenIdCardUrlName = saveImageToFileSystem(citizenIdCardUrl); // Lưu ảnh và lấy đường dẫn
                user.setCitizenIdCardUrl(citizenIdCardUrlName);

            } catch (IllegalArgumentException e)
            {
                user.setCitizenIdCardUrl(profileRequest.getCitizenIdCardUrl());

            }
        }



        return userRepository.save(user);
    }

    private String saveImageToFileSystem(byte[] imageBytes) throws IOException {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("Dữ liệu ảnh không được để trống");
        }
        if (imageBytes.length > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Kích thước ảnh vượt quá giới hạn tối đa 10MB.");
        }
        String fileName = UUID.randomUUID().toString() + ".png";
        Path path = Paths.get("/uploads/images/" + fileName);
        Files.createDirectories(path.getParent());
        Files.write(path, imageBytes);
        return fileName;
    }
    @Override
    public void resetPassword(String token, HttpServletResponse response, AuthenRequest changePasswordRequest) {
        Token theToken = tokenRepository.findByAccessToken(token)
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.TOKEN_INVALID));
        if (theToken != null) {
            User user = theToken.getUser();
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(user);
        }
    }

    @Override
    public Object rentalList(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status) {
        return rentalRequestRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            User user = SecurityUtils.getCurrentUser().orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));

            predicates.add(cb.equal(root.get("paymentStatus"), true));

            if (user.getId() != null) {
                predicates.add(cb.equal(root.get("customerId"), user.getId()));
            }
            if (brands != null && !brands.isEmpty()) {
                predicates.add(root.get("brandId").in(brands));
            }
            if (categories != null && !categories.isEmpty()) {
                predicates.add(root.get("categoryId").in(categories));
            }
            if (StringUtils.hasText(vehicleName)) {
                predicates.add(cb.like(cb.lower(root.get("vehicleName")), "%" + vehicleName.toLowerCase() + "%"));
            }
            if (StringUtils.hasText(status)) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (startDate != null && endDate != null) {
                // Điều kiện khoảng thời gian giao nhau
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), endDate));
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), startDate));
            } else if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("endDate"), startDate));
            } else if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startDate"), endDate));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Override
    public Page<Review> getReviewsByVehicleId(String vehicleId, Pageable pageable) {
        // Kiểm tra xem xe có tồn tại không
        if (!vehicleRepository.existsById(vehicleId)) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.VEHICLE_NOT_FOUND);
        }
        return reviewRepository.findByVehicleId(vehicleId, pageable);
    }

    @Override
    @Transactional
    public Review updateReview(String reviewId, UpdateReviewRequest request) {
        User currentUser = SecurityUtils.getCurrentUser()
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.REVIEW_NOT_FOUND));

        // Kiểm tra người dùng hiện tại có phải là tác giả của review không
        if (!review.getUserId().equals(currentUser.getId())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.NOT_REVIEW_AUTHOR);
        }

        // Validate rating
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.INVALID_RATING);
        }

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        // createdAt không đổi, updatedAt sẽ tự động cập nhật bởi @UpdateTimestamp

        Review updatedReview = reviewRepository.save(review);


        return updatedReview;
    }


    @Override
    @Transactional
    public void deleteReview(String reviewId) {
        User currentUser = SecurityUtils.getCurrentUser()
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));

        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.REVIEW_NOT_FOUND));

        // Kiểm tra quyền sở hữu
        if (!review.getUserId().equals(currentUser.getId())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.NOT_REVIEW_AUTHOR);
        }

        String vehicleId = review.getVehicleId();
        reviewRepository.delete(review);

    }

    @Override
    @Transactional
    public Review createReview(CreateReviewRequest request) {
        User currentUser = SecurityUtils.getCurrentUser()
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));

//        // 1. Tìm đơn thuê xe
//        RentalRequest rentalRequest = rentalRequestRepository.findById(request.getBookingId())
//                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.RENTAL_REQUEST_NOT_FOUND));

//        // 2. Kiểm tra người dùng có phải là người thuê không
//        if (!rentalRequest.getCustomerId().equals(currentUser.getId())) {
//            throw PvrsClientException.ofHandler(PvrsErrorHandler.NOT_ALLOW_TO_BOOK_VEHICLE);
//        }

        // 3. Kiểm tra đơn thuê đã hoàn thành chưa
//        if (!RentalStatus.COMPLETED.name().equals(rentalRequest.getStatus())) {
//            throw PvrsClientException.ofHandler(PvrsErrorHandler.RENTAL_REQUEST_NOT_COMPLETED);
//        }

//        // 4. Kiểm tra xem đơn thuê này đã được đánh giá chưa
//        if (reviewRepository.existsByBookingId(request.getBookingId())) {
//            throw PvrsClientException.ofHandler(PvrsErrorHandler.REVIEW_ALREADY_EXISTS);
//        }

        // 5. Validate rating
        if (request.getRating() < 1 || request.getRating() > 5) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.INVALID_RATING);
        }

        // 6. Tạo và lưu review
        Review newReview = new Review();
        newReview.setUserId(currentUser.getId());
        newReview.setVehicleId(request.getVehicleId());
//        newReview.setBookingId(rentalRequest.getId());
        newReview.setRating(request.getRating());
        newReview.setComment(request.getComment());

        Review savedReview = reviewRepository.save(newReview);


        return savedReview;
    }

    public Double calculateAverageRating(String vehicleId) {
        // Kiểm tra xem xe có tồn tại không để trả về lỗi 404 nếu cần
        if (!vehicleRepository.existsById(vehicleId)) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.VEHICLE_NOT_FOUND);
        }

        // Gọi hàm từ repository
        Double average = reviewRepository.findAverageRatingByVehicleId(vehicleId);

        // Nếu không có review nào, hàm AVG sẽ trả về NULL.
        // Trong trường hợp này, chúng ta trả về 0.0 cho an toàn.
        if (average == null) {
            return 0.0;
        }

        return average;
    }


    @Override
    public Object payLateFee(String id) throws Exception {
        long orderCode = System.currentTimeMillis();
        RentalRequest request = rentalRequestRepository.getReferenceById(id) ;
        request.setOrderCode(orderCode);
        User user = userRepository.getReferenceById(request.getCustomerId()) ;
        PayOS payOS = new PayOS("65f3da88-9fa3-4ef3-8a5d-9393a14d3b84","c0473543-c34c-4601-8f23-88fc7fec2645","f3d3ea900cf6dd94e57b86b3c09ae3946ab397569ea3a930559a1c2a0797f25f") ;
        PaymentData paymentData = PaymentData.builder()
                .orderCode(orderCode)
                .amount(request.getLateFee().intValue())
                .description("Thanh toán phí muộn " + user.getUsername())
                .returnUrl("http://localhost:8080/v1/user/late/success?orderCode=" + orderCode) // Nếu thanh toán thành công
                .cancelUrl("http://localhost:8080/v1/user/late/failed?orderCode=" + orderCode) // Nếu hủy không thanh toán
                .build();

        request.setUrl(payOS.createPaymentLink(paymentData).getCheckoutUrl());


        return rentalRequestRepository.save(request);
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

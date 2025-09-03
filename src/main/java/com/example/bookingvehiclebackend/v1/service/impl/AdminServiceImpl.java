package com.example.bookingvehiclebackend.v1.service.impl;

import com.example.bookingvehiclebackend.config.JwtService;
import com.example.bookingvehiclebackend.v1.dto.*;
import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import com.example.bookingvehiclebackend.v1.event.RegistrationCompleteEvent;
import com.example.bookingvehiclebackend.v1.exception.PvrsClientException;
import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import com.example.bookingvehiclebackend.v1.repository.*;
import com.example.bookingvehiclebackend.v1.service.AdminService;
import com.example.bookingvehiclebackend.v1.utils.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CategoryRepository categoryRepository;
    private final JwtService jwtService;
    private final ApplicationEventPublisher publisher;

    private final BrandRepository brandRepository;

    private final VehicleRepository vehicleRepository;

    private final RentalRequestRepository rentalRequestRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TrackingLogRepository trackingLogRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Object createVehicle(CreateVehicleRequest request) throws IOException {
        if (!SecurityUtils.hasRole(Role.ADMIN, Role.OWNER)) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.NOT_ALLOW_TO_CREATE_VEHICLE);
        }
        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
        if (vehicleRepository.findByLiecensePlate(request.getLicensePlate()).isPresent()) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.VEHICLE_EXISTED);
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleName(request.getName());
        vehicle.setBranchId(request.getBrand());
        vehicle.setCategoryId(request.getCategory());
        vehicle.setFuelType(request.getType());
        vehicle.setSeatCount(request.getSeats());
        vehicle.setPricePerDay(request.getDailyPrice());
        vehicle.setPricePerHour(request.getHourlyPrice());
        vehicle.setLiecensePlate(request.getLicensePlate());
        vehicle.setDescription(request.getDescription());
        vehicle.setStatus(VehicleStatus.AVAILABLE.name());
        vehicle.setOwnerId(Role.OWNER.equals(user.getRole()) ? user.getId() : null);
        vehicle.setLiecensePlate(request.getLicensePlate());
        vehicle.setCreatedBy(user.getUsername());
        vehicle.setUserId(user.getId());
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setDescription(request.getDescription());
        vehicle.setGearBox(request.getGearbox());
        vehicle.setLocation(request.getLocation());
        vehicle.setVehicleTypeId(request.getVehicleTypeId());
        vehicle.setApproved(null);



        String imageUrlName = null;
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            String[] imageParts = request.getImageUrl().split(",");
            List<String> savedImageUrls = new ArrayList<>();

            for (String imagePart : imageParts) {
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(imagePart.trim());
                    String fileName = saveImageToFileSystem(imageBytes);
                    savedImageUrls.add(fileName);
                } catch (IllegalArgumentException e) {
                    // Nếu không phải base64, coi như là URL trực tiếp
                    savedImageUrls.add(imagePart.trim());
                }
            }

            // Lưu dưới dạng chuỗi ngăn cách bởi dấu phẩy
            vehicle.setImageUrl(String.join(",", savedImageUrls));
        }


        if (request.getRegistrationDocumentUrl() != null && !request.getRegistrationDocumentUrl().isEmpty()) {
            String[] imageParts = request.getRegistrationDocumentUrl().split(",");
            List<String> savedImageUrls = new ArrayList<>();

            for (String imagePart : imageParts) {
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(imagePart.trim());
                    String fileName = saveImageToFileSystem(imageBytes);
                    savedImageUrls.add(fileName);
                } catch (IllegalArgumentException e) {
                    // Nếu không phải base64, coi như là URL trực tiếp
                    savedImageUrls.add(imagePart.trim());
                }
            }

            // Lưu dưới dạng chuỗi ngăn cách bởi dấu phẩy
            vehicle.setRegistrationDocumentUrl(String.join(",", savedImageUrls));
        }



        return vehicleRepository.save(vehicle);
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
    public List<Category> categoryList() {
        return categoryRepository.findAll();
    }

    @Override
    public List<Brand> brandList() {
        return brandRepository.findAll();
    }

    @Override
    public Object approveBooking(String id) {
        RentalRequest booking = rentalRequestRepository.findById(id)
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));
//        if (!RentalStatus.PENDING.name().equals(booking.getStatus())) {
//            throw PvrsClientException.ofHandler(PvrsErrorHandler.BOOKING_IS_NOT_PENDING_STATUS);
//        }
        booking.setStatus(RentalStatus.APPROVED.name());
        booking.setDeliveryStatus(DeliveryStatus.TRANSIT.name());
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId()).orElse(null);
        assert vehicle != null;
        vehicle.setStatus("RENTED");
        return rentalRequestRepository.save(booking);
    }

    @Override
    public Object searchVehicles(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status,String fuelType) {
        Specification<Vehicle> spec = new Specification<Vehicle>() {
            @Override
            public Predicate toPredicate(Root<Vehicle> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                // Subquery để kiểm tra xung đột thời gian
                Subquery<String> subquery = query.subquery(String.class);
                Root<RentalRequest> rentalRequestRoot = subquery.from(RentalRequest.class);
                subquery.select(rentalRequestRoot.get("vehicleId"));

                // Sử dụng giá trị startDate và endDate trực tiếp thay vì tham số
                if (startDate != null && endDate != null) {
                    subquery.where(cb.and(
                            cb.equal(rentalRequestRoot.get("vehicleId"), root.get("id")),
                            cb.or(
                                    // Xung đột khi startDate nằm trong khoảng đã thuê
                                    cb.and(
                                            cb.greaterThanOrEqualTo(cb.literal(startDate), rentalRequestRoot.get("startDate")),
                                            cb.lessThanOrEqualTo(cb.literal(startDate), rentalRequestRoot.get("endDate"))
                                    ),
                                    // Xung đột khi endDate nằm trong khoảng đã thuê
                                    cb.and(
                                            cb.greaterThanOrEqualTo(cb.literal(endDate), rentalRequestRoot.get("startDate")),
                                            cb.lessThanOrEqualTo(cb.literal(endDate), rentalRequestRoot.get("endDate"))
                                    ),
                                    // Xung đột khi khoảng thời gian bao phủ toàn bộ khoảng đã thuê
                                    cb.and(
                                            cb.lessThanOrEqualTo(cb.literal(startDate), rentalRequestRoot.get("startDate")),
                                            cb.greaterThanOrEqualTo(cb.literal(endDate), rentalRequestRoot.get("endDate"))
                                    )
                            ),
                            cb.notEqual(rentalRequestRoot.get("status"), "CANCELLED") // Loại bỏ các yêu cầu đã hủy
                    ));
                } else {
                    // Nếu không có startDate hoặc endDate, không áp dụng điều kiện thời gian
                    subquery.where(cb.and(
                            cb.equal(rentalRequestRoot.get("vehicleId"), root.get("id")),
                            cb.notEqual(rentalRequestRoot.get("status"), "CANCELLED")
                    ));
                }

                // Điều kiện cơ bản


                if (brands != null && !brands.isEmpty()) {
                    predicates.add(root.get("branchId").in(brands));
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

                // Thêm điều kiện không có xung đột thời gian
                if (startDate != null && endDate != null) {
                    predicates.add(cb.not(root.get("id").in(subquery)));
                }

                if (StringUtils.hasText(fuelType)) {
                    predicates.add(cb.equal(root.get("fuelType"), fuelType));
                }

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };

        Page<Vehicle> page = vehicleRepository.findAll(spec, pageable);
        return page;
    }

    @Override
    public Object viewVehicle(String id) {
        return vehicleRepository.findById(id).orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));
    }

    @Override
    public Object updateVehicle(CreateVehicleRequest request) throws IOException {
        Vehicle vehicle = vehicleRepository.findById(request.getId()).orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));
        vehicle.setVehicleName(request.getName());
        vehicle.setBranchId(request.getBrand());
        vehicle.setCategoryId(request.getCategory());
        vehicle.setFuelType(request.getType());
        vehicle.setSeatCount(request.getSeats());
        vehicle.setPricePerDay(request.getDailyPrice());
        vehicle.setPricePerHour(request.getHourlyPrice());
        vehicle.setLiecensePlate(request.getLicensePlate());
        vehicle.setDescription(request.getDescription());
        vehicle.setLiecensePlate(request.getLicensePlate());
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setDescription(request.getDescription());
        vehicle.setGearBox(request.getGearbox());
        vehicle.setLocation(request.getLocation());
        vehicle.setVehicleTypeId(request.getVehicleTypeId());
        vehicle.setApproved(null);



        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            String[] imageParts = request.getImageUrl().split(",");
            List<String> savedImageUrls = new ArrayList<>();

            for (String imagePart : imageParts) {
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(imagePart.trim());
                    String fileName = saveImageToFileSystem(imageBytes);
                    savedImageUrls.add(fileName);
                } catch (IllegalArgumentException e) {
                    // Nếu không phải base64, coi như là URL trực tiếp
                    savedImageUrls.add(imagePart.trim());
                }
            }

            // Lưu dưới dạng chuỗi ngăn cách bởi dấu phẩy
            vehicle.setImageUrl(String.join(",", savedImageUrls));
        }

        if (request.getRegistrationDocumentUrl() != null && !request.getRegistrationDocumentUrl().isEmpty()) {
            String[] imageParts = request.getRegistrationDocumentUrl().split(",");
            List<String> savedImageUrls = new ArrayList<>();

            for (String imagePart : imageParts) {
                try {
                    byte[] imageBytes = Base64.getDecoder().decode(imagePart.trim());
                    String fileName = saveImageToFileSystem(imageBytes);
                    savedImageUrls.add(fileName);
                } catch (IllegalArgumentException e) {
                    // Nếu không phải base64, coi như là URL trực tiếp
                    savedImageUrls.add(imagePart.trim());
                }
            }

            // Lưu dưới dạng chuỗi ngăn cách bởi dấu phẩy
            vehicle.setRegistrationDocumentUrl(String.join(",", savedImageUrls));
        }




        return vehicleRepository.save(vehicle);
    }

    @Override
    public void deleteVehicle(CreateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getId()).orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));
        List<RentalRequest> rentalRequest = rentalRequestRepository.findByVehicleId(vehicle.getId());
        rentalRequestRepository.deleteAll(rentalRequest);
        vehicleRepository.delete(vehicle);
    }

    @Override
    public Object rentalList(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status, String fuelType) {
        return rentalRequestRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("paymentStatus"), true));

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
    public List<VehicleType> vehicleTypeList() {
        return vehicleTypeRepository.findAll();
    }

    @Override
    public Object rejectBooking(String id) {
        RentalRequest booking = rentalRequestRepository.findById(id)
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));
        if (!RentalStatus.PENDING.name().equals(booking.getStatus())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.BOOKING_IS_NOT_PENDING_STATUS);
        }
        booking.setStatus(RentalStatus.REJECTED.name());
        booking.setDeliveryStatus(null);
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId()).orElse(null);
        assert vehicle != null;
        vehicle.setStatus("AVAILABLE");
        return rentalRequestRepository.save(booking);
    }

    @Override
    public void approveVehicle(CreateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getId()).orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));
        vehicle.setApproved(true);
        vehicle.setReason("");
        vehicleRepository.save(vehicle);
    }



    @Override
    public Object searchVehiclesIsApproved(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status,String fuelType) {
        Specification<Vehicle> spec = new Specification<Vehicle>() {
            @Override
            public Predicate toPredicate(Root<Vehicle> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                // Subquery để kiểm tra xung đột thời gian
                Subquery<String> subquery = query.subquery(String.class);
                Root<RentalRequest> rentalRequestRoot = subquery.from(RentalRequest.class);
                subquery.select(rentalRequestRoot.get("vehicleId"));

                // Sử dụng giá trị startDate và endDate trực tiếp thay vì tham số
                if (startDate != null && endDate != null) {
                    subquery.where(cb.and(
                            cb.equal(rentalRequestRoot.get("vehicleId"), root.get("id")),
                            cb.or(
                                    // Xung đột khi startDate nằm trong khoảng đã thuê
                                    cb.and(
                                            cb.greaterThanOrEqualTo(cb.literal(startDate), rentalRequestRoot.get("startDate")),
                                            cb.lessThanOrEqualTo(cb.literal(startDate), rentalRequestRoot.get("endDate"))
                                    ),
                                    // Xung đột khi endDate nằm trong khoảng đã thuê
                                    cb.and(
                                            cb.greaterThanOrEqualTo(cb.literal(endDate), rentalRequestRoot.get("startDate")),
                                            cb.lessThanOrEqualTo(cb.literal(endDate), rentalRequestRoot.get("endDate"))
                                    ),
                                    // Xung đột khi khoảng thời gian bao phủ toàn bộ khoảng đã thuê
                                    cb.and(
                                            cb.lessThanOrEqualTo(cb.literal(startDate), rentalRequestRoot.get("startDate")),
                                            cb.greaterThanOrEqualTo(cb.literal(endDate), rentalRequestRoot.get("endDate"))
                                    )
                            ),
                            cb.notEqual(rentalRequestRoot.get("status"), "CANCELLED") // Loại bỏ các yêu cầu đã hủy
                    ));
                } else {
                    // Nếu không có startDate hoặc endDate, không áp dụng điều kiện thời gian
                    subquery.where(cb.and(
                            cb.equal(rentalRequestRoot.get("vehicleId"), root.get("id")),
                            cb.notEqual(rentalRequestRoot.get("status"), "CANCELLED")
                    ));
                }

                // Điều kiện cơ bản
                predicates.add(root.get("approved").in(true));

                if (brands != null && !brands.isEmpty()) {
                    predicates.add(root.get("branchId").in(brands));
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

                // Thêm điều kiện không có xung đột thời gian
                if (startDate != null && endDate != null) {
                    predicates.add(cb.not(root.get("id").in(subquery)));
                }

                if (StringUtils.hasText(fuelType)) {
                    predicates.add(cb.equal(root.get("fuelType"), fuelType));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };

        Page<Vehicle> page = vehicleRepository.findAll(spec, pageable);
        return page;
    }

    @Override
    public Object createBrand(Brand brand) {
        // Kiểm tra xem name đã tồn tại trong database chưa
        if (brandRepository.existsByName(brand.getName())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.EXIST_BRANCH);
        }
        return brandRepository.save(brand);
    }

    @Override
    public Object updateBrand(String id, Brand brand) {
        Brand brand1 = brandRepository.findById(id).orElse(null);

        assert brand1 != null;
        if (brandRepository.existsByName(brand.getName())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.EXIST_BRANCH);
        }
        brand1.setName(brand.getName());
        return brandRepository.save(brand1);
    }

    @Override
    public void deleteBrand(String id) {
        brandRepository.deleteById(id);
    }

    @Override
    public Object createCategory(Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.EXIST_CATEGORY);
        }
        return categoryRepository.save(category);
    }

    @Override
    public Object updateCategory(String id, Category category) {
        Category category1 = categoryRepository.findById(id).orElse(null);

        assert category1 != null;
        if (categoryRepository.existsByName(category.getName())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.EXIST_CATEGORY);
        }
        category1.setName(category.getName());
        return categoryRepository.save(category1);
    }

    @Override
    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }

    @Override
    public List<User> getUserList() {
        return userRepository.findAll() ;
    }



    @Override
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    @Override
    public Object updateUserRole(String id, User user) {
        User  user1 = userRepository.findById(id).orElse(null);
        assert user1 != null;
        user1.setRole(user.getRole());
        return userRepository.save(user1);
    }

    @Override
    public void rejectVehicle(CreateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getId()).orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));
        vehicle.setApproved(false);
        vehicle.setReason(request.getReason());
        vehicleRepository.save(vehicle);
    }

    @Override
    public Object returnedBooking(String id) {
        RentalRequest booking = rentalRequestRepository.findById(id)
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));

        booking.setReturnDate(LocalDateTime.now());
        booking.setStatus(RentalStatus.AVAILABLE.name());
        booking.setDeliveryStatus(DeliveryStatus.CONFIRM_RETURNED.name());
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId()).orElse(null);
        assert vehicle != null;
        vehicle.setStatus("AVAILABLE");
        return rentalRequestRepository.save(booking);    }

    @Override
    public Object deliveredBooking(String id) {
        RentalRequest booking = rentalRequestRepository.findById(id)
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));

        booking.setStatus(RentalStatus.AVAILABLE.name());
        booking.setDeliveryStatus(DeliveryStatus.DELIVERED.name());
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId()).orElse(null);
        assert vehicle != null;
        vehicle.setStatus("RENTED");
        return rentalRequestRepository.save(booking);
    }

    @Override
    public Object returnBooking(String id) {
        RentalRequest booking = rentalRequestRepository.findById(id)
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));

        booking.setStatus(RentalStatus.AVAILABLE.name());
        booking.setDeliveryStatus(DeliveryStatus.RETURNED.name());
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId()).orElse(null);
        assert vehicle != null;
        vehicle.setStatus("RENTED");
        return rentalRequestRepository.save(booking);
    }

    @Override
    public Object getListUser() {
        return null;
    }

    @Override
    public Object createUserList(AuthenRequest request,HttpServletRequest httpServletRequest) {
        String password = request.getPassword();
        if (!isValidPassword(password)) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.INVALID_PASSWORD_FORMAT);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.USER_IS_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.EMAIL_IS_EXISTED);
        }
        if (ObjectUtils.isEmpty(request.getEmail())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.EMAIL_NOT_FOUND);
        }

        // Khi user dang ki chua xac thuc mail -> flagActive = INACTIVE
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(Instant.now());
//        user.setFlagActive("ACTIVE");
        user.setFlagActive("INACTIVE");
        user.setRole(request.getRole());
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        // Sau khi dang ki thanh cong, can xac thuc qua email
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(httpServletRequest), jwtToken));
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setRefreshToken(refreshToken);
        return loginResponse;

    }

    @Override
    public Object searchVehiclesByUser(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status, String fuelType) {
        Specification<Vehicle> spec = new Specification<Vehicle>() {
            @Override
            public Predicate toPredicate(Root<Vehicle> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                // Subquery để kiểm tra xung đột thời gian
                Subquery<String> subquery = query.subquery(String.class);
                Root<RentalRequest> rentalRequestRoot = subquery.from(RentalRequest.class);
                subquery.select(rentalRequestRoot.get("vehicleId"));

                // Sử dụng giá trị startDate và endDate trực tiếp thay vì tham số
                if (startDate != null && endDate != null) {
                    subquery.where(cb.and(
                            cb.equal(rentalRequestRoot.get("vehicleId"), root.get("id")),
                            cb.or(
                                    // Xung đột khi startDate nằm trong khoảng đã thuê
                                    cb.and(
                                            cb.greaterThanOrEqualTo(cb.literal(startDate), rentalRequestRoot.get("startDate")),
                                            cb.lessThanOrEqualTo(cb.literal(startDate), rentalRequestRoot.get("endDate"))
                                    ),
                                    // Xung đột khi endDate nằm trong khoảng đã thuê
                                    cb.and(
                                            cb.greaterThanOrEqualTo(cb.literal(endDate), rentalRequestRoot.get("startDate")),
                                            cb.lessThanOrEqualTo(cb.literal(endDate), rentalRequestRoot.get("endDate"))
                                    ),
                                    // Xung đột khi khoảng thời gian bao phủ toàn bộ khoảng đã thuê
                                    cb.and(
                                            cb.lessThanOrEqualTo(cb.literal(startDate), rentalRequestRoot.get("startDate")),
                                            cb.greaterThanOrEqualTo(cb.literal(endDate), rentalRequestRoot.get("endDate"))
                                    )
                            ),
                            cb.notEqual(rentalRequestRoot.get("status"), "CANCELLED") // Loại bỏ các yêu cầu đã hủy
                    ));
                } else {
                    // Nếu không có startDate hoặc endDate, không áp dụng điều kiện thời gian
                    subquery.where(cb.and(
                            cb.equal(rentalRequestRoot.get("vehicleId"), root.get("id")),
                            cb.notEqual(rentalRequestRoot.get("status"), "CANCELLED")
                    ));
                }

                // Điều kiện cơ bản
                User currentUser = SecurityUtils.getCurrentUser()
                        .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
                predicates.add(root.get("userId").in(currentUser.getId()));


                if (brands != null && !brands.isEmpty()) {
                    predicates.add(root.get("branchId").in(brands));
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

                // Thêm điều kiện không có xung đột thời gian
                if (startDate != null && endDate != null) {
                    predicates.add(cb.not(root.get("id").in(subquery)));
                }

                if (StringUtils.hasText(fuelType)) {
                    predicates.add(cb.equal(root.get("fuelType"), fuelType));
                }



                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };

        Page<Vehicle> page = vehicleRepository.findAll(spec, pageable);
        return page;
    }

    @Override
    public Object trackingLog(TrackingLog trackingLog) {
        return trackingLogRepository.save(trackingLog);
    }

    private boolean isValidPassword(String password) {
        // Ít nhất 1 chữ hoa, 1 chữ số và 1 ký tự đặc biệt
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&^#()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/~`]).{8,}$";
        return password != null && password.matches(regex);
    }
    public String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}



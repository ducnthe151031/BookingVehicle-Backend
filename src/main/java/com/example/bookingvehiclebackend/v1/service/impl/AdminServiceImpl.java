package com.example.bookingvehiclebackend.v1.service.impl;

import com.example.bookingvehiclebackend.v1.dto.*;
import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;
import com.example.bookingvehiclebackend.v1.exception.PvrsClientException;
import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import com.example.bookingvehiclebackend.v1.repository.*;
import com.example.bookingvehiclebackend.v1.service.AdminService;
import com.example.bookingvehiclebackend.v1.utils.SecurityUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;

    private final VehicleRepository vehicleRepository;

    private final RentalRequestRepository rentalRequestRepository;
    private final VehicleTypeRepository vehicleTypeRepository;

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
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setDescription(request.getDescription());
        vehicle.setGearBox(request.getGearbox());
        vehicle.setLocation(request.getLocation());
        vehicle.setVehicleTypeId(request.getVehicleTypeId());
        vehicle.setApproved(false);



        String imageUrlName = null;
        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            byte[] imageUrl = Base64.getDecoder().decode(request.getImageUrl());
            imageUrlName = saveImageToFileSystem(imageUrl); // Lưu ảnh và lấy đường dẫn
            vehicle.setImageUrl(imageUrlName);

        }

        String registrationDocumentName = null;
        if (request.getRegistrationDocumentUrl() != null && !request.getRegistrationDocumentUrl().isEmpty()) {
            byte[] registrationDocument = Base64.getDecoder().decode(request.getRegistrationDocumentUrl());
            registrationDocumentName = saveImageToFileSystem(registrationDocument); // Lưu ảnh và lấy đường dẫn
            vehicle.setRegistrationDocumentUrl(registrationDocumentName);

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
        if (!RentalStatus.PENDING.name().equals(booking.getStatus())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.BOOKING_IS_NOT_PENDING_STATUS);
        }
        booking.setStatus(RentalStatus.APPROVED.name());
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId()).orElse(null);
        assert vehicle != null;
        vehicle.setStatus("RENTED");
        return rentalRequestRepository.save(booking);
    }

    @Override
    public Object searchVehicles(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status) {
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
        vehicle.setApproved(false);


        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            try {
                byte[] imageUrl = Base64.getDecoder().decode(request.getImageUrl());
                String imageUrlName = saveImageToFileSystem(imageUrl); // Lưu ảnh và lấy đường dẫn
                vehicle.setImageUrl(imageUrlName);

            } catch (IllegalArgumentException e)
            {
                vehicle.setImageUrl(request.getImageUrl());

            }
        }

        if (request.getRegistrationDocumentUrl() != null && !request.getRegistrationDocumentUrl().isEmpty()) {
            try {
                byte[] registrationDocument = Base64.getDecoder().decode(request.getRegistrationDocumentUrl());
                String registrationDocumentName = saveImageToFileSystem(registrationDocument); // Lưu ảnh và lấy đường dẫn
                vehicle.setRegistrationDocumentUrl(registrationDocumentName);

            } catch (IllegalArgumentException e)
            {
                vehicle.setRegistrationDocumentUrl(request.getRegistrationDocumentUrl());

            }
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
    public Object rentalList(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status) {
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
        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId()).orElse(null);
        assert vehicle != null;
        vehicle.setStatus("AVAILABLE");
        return rentalRequestRepository.save(booking);
    }

    @Override
    public void approveVehicle(CreateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getId()).orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));
        vehicle.setApproved(true);
        vehicleRepository.save(vehicle);
    }

    @Override
    public Object searchVehiclesIsApproved(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status) {
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

                return cb.and(predicates.toArray(new Predicate[0]));
            }
        };

        Page<Vehicle> page = vehicleRepository.findAll(spec, pageable);
        return page;
    }
}

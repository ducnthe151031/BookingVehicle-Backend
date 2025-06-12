package com.example.bookingvehiclebackend.v1.service.impl;

import com.example.bookingvehiclebackend.v1.dto.*;
import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;
import com.example.bookingvehiclebackend.v1.exception.PvrsClientException;
import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import com.example.bookingvehiclebackend.v1.repository.BrandRepository;
import com.example.bookingvehiclebackend.v1.repository.CategoryRepository;
import com.example.bookingvehiclebackend.v1.repository.RentalRequestRepository;
import com.example.bookingvehiclebackend.v1.repository.VehicleRepository;
import com.example.bookingvehiclebackend.v1.service.AdminService;
import com.example.bookingvehiclebackend.v1.utils.SecurityUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final CategoryRepository categoryRepository;

    private final BrandRepository brandRepository;

    private final VehicleRepository vehicleRepository;

    private final RentalRequestRepository rentalRequestRepository;

    @Override
    public Object createVehicle(CreateVehicleRequest request) {
        if (!SecurityUtils.hasRole(Role.ADMIN, Role.OWNER)) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.NOT_ALLOW_TO_CREATE_VEHICLE);
        }
        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleName(request.getName());
        vehicle.setBranchId(request.getBrand());
        vehicle.setCategoryId(request.getCategory());
        vehicle.setFuelType(request.getType());
        vehicle.setSeatCount(request.getSeats());
        vehicle.setPricePerDay(request.getDaily_price());
        vehicle.setLiecensePlate(request.getLicensePlate());
        vehicle.setDescription(request.getDescription());
        vehicle.setStatus(VehicleStatus.AVAILABLE.name());
        vehicle.setOwnerId(Role.OWNER.equals(user.getRole()) ? user.getId() : null);
        vehicle.setLiecensePlate(request.getLicensePlate());
        vehicle.setCreatedBy(user.getUsername());
        vehicle.setCreatedAt(LocalDateTime.now());
        vehicle.setDescription(request.getDescription());
        return vehicleRepository.save(vehicle);
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
        if(!RentalStatus.PENDING.name().equals(booking.getStatus())){
            throw PvrsClientException.ofHandler(PvrsErrorHandler.BOOKING_IS_NOT_PENDING_STATUS);
        }
        booking.setStatus(RentalStatus.APPROVED.name());
        return rentalRequestRepository.save(booking);
    }

    @Override
    public Object searchVehicles(List<String> brands, List<String> categories, String vehicleName, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable, String status) {
        return vehicleRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

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
            // Giả sử bạn có bảng Booking (RentalRequest) và muốn lọc xe chưa được thuê trong khoảng này
            if (startDate != null && endDate != null) {
                Subquery<String> subquery = query.subquery(String.class);
                Root<RentalRequest> rentalRoot = subquery.from(RentalRequest.class);
                subquery.select(rentalRoot.get("vehicleId"));
                subquery.where(
                        cb.and(
                                cb.lessThan(rentalRoot.get("startDate"), endDate),
                                cb.greaterThan(rentalRoot.get("endDate"), startDate)
                        )
                );
                predicates.add(cb.not(root.get("id").in(subquery)));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Override
    public Object viewVehicle(String id) {
        return  vehicleRepository.findById(id).orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));
    }

    @Override
    public Object updateVehicle(CreateVehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findById(request.getId()).orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.VEHICLE_NOT_FOUND));
        vehicle.setVehicleName(request.getName());
        vehicle.setPricePerDay(request.getDaily_price());
        vehicle.setFuelType(request.getType());
        vehicle.setSeatCount(request.getSeats());
        vehicle.setImageUrl(Arrays.toString(request.getImage()));
        vehicle.setCategoryId(request.getCategory());
        vehicle.setBranchId(request.getBrand());
        vehicle.setLiecensePlate(request.getLicensePlate());
        vehicle.setDescription(request.getDescription());
        return vehicleRepository.save(vehicle);
    }
}

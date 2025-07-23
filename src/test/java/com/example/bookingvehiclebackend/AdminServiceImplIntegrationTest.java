package com.example.bookingvehiclebackend;

import com.example.bookingvehiclebackend.v1.dto.request.CreateVehicleRequest;
import com.example.bookingvehiclebackend.v1.dto.*;
import com.example.bookingvehiclebackend.v1.repository.*;
import com.example.bookingvehiclebackend.v1.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AdminServiceImplIntegrationTest {

    @Autowired
    private AdminService adminService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleTypeRepository vehicleTypeRepository;

    @Test
    void testCategoryList() {
        List<Category> categories = adminService.categoryList();
        assertNotNull(categories);
    }

    @Test
    void testBrandList() {
        List<Brand> brands = adminService.brandList();
        assertNotNull(brands);
    }

    @Test
    void testVehicleTypeList() {
        List<VehicleType> types = adminService.vehicleTypeList();
        assertNotNull(types);
    }

    @Test
    void testCreateAndViewVehicle() throws Exception {
        CreateVehicleRequest request = new CreateVehicleRequest();
        request.setName("BMW M4");
        request.setBrand("1");
        request.setCategory("1");
        request.setType("Gasoline");
        request.setSeats(4);
        request.setDailyPrice(BigDecimal.valueOf(4000000.00));
        request.setHourlyPrice(BigDecimal.valueOf(250000.00));
        request.setLicensePlate("30A-12355");
        request.setDescription("BMW M4 2020");
        request.setGearbox("MANUAL");
        request.setLocation("Hà Nội");
        request.setVehicleTypeId("1");
        // Set image and registration document as base64 if needed

        Object created = adminService.createVehicle(request);
        assertNotNull(created);

        // Assuming the created object has an ID
        String vehicleId = ((Vehicle) created).getId();
        Object found = adminService.viewVehicle(vehicleId);
        assertNotNull(found);
    }

    @Test
    void testUpdateVehicle() throws Exception {
        // First, create a vehicle
        CreateVehicleRequest request = new CreateVehicleRequest();
        request.setName("Test Car");
        request.setBrand("brandId");
        request.setCategory("categoryId");
        request.setType("Gasoline");
        request.setSeats(4);
        request.setDailyPrice(BigDecimal.valueOf(2000000.0));
        request.setHourlyPrice(BigDecimal.valueOf(100000.0));
        request.setLicensePlate("TEST-123");
        request.setDescription("Test vehicle");
        request.setGearbox("Automatic");
        request.setLocation("Test City");
        request.setVehicleTypeId("typeId");

        Vehicle created = (Vehicle) adminService.createVehicle(request);

        // Update
        request.setId(created.getId());
        request.setName("Updated Car");
        Object updated = adminService.updateVehicle(request);
        assertNotNull(updated);
        assertEquals("Updated Car", ((Vehicle) updated).getVehicleName());
    }

    @Test
    void testDeleteVehicle() throws Exception {
        // Create a vehicle
        CreateVehicleRequest request = new CreateVehicleRequest();
        request.setName("Test Car");
        request.setBrand("brandId");
        request.setCategory("categoryId");
        request.setType("Gasoline");
        request.setSeats(4);
        request.setDailyPrice(BigDecimal.valueOf(2000000.0));
        request.setHourlyPrice(BigDecimal.valueOf(100000.0));
        request.setLicensePlate("TEST-123");
        request.setDescription("Test vehicle");
        request.setGearbox("Automatic");
        request.setLocation("Test City");
        request.setVehicleTypeId("typeId");

        Vehicle created = (Vehicle) adminService.createVehicle(request);

        // Delete
        request.setId(created.getId());
        adminService.deleteVehicle(request);

        assertFalse(vehicleRepository.findById(created.getId()).isPresent());
    }

    @Test
    void testSearchVehicles() {
        Object result = adminService.searchVehicles(
                List.of(), List.of(), null,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                PageRequest.of(0, 10), null
        );
        assertNotNull(result);
    }

    @Test
    void testSearchVehiclesIsApproved() {
        Object result = adminService.searchVehiclesIsApproved(
                List.of(), List.of(), null,
                LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                PageRequest.of(0, 10), null
        );
        assertNotNull(result);
    }
}
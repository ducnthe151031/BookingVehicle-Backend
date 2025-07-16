package com.example.bookingvehiclebackend.v1.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateVehicleRequest {
    private String id;
    private String name; // tên xe
    private String location; // địa điểm xe đặt tại
    private BigDecimal dailyPrice; // Giá thuê theo ngày
    private String type; // Loại nhiên liệu: Diesel, Gasoline, Electric, Hybrid, Unknown
    private String gearbox; // Manual hoặc Automatic
    private Integer seats; // Số ghe ngồi
    private MultipartFile[] image; // Ảnh đại diện của xe
    private String category;
    private String brand;
    private String licensePlate;
    private String description;
    private BigDecimal hourlyPrice;
    private String vehicleTypeId;
    private String imageUrl;
    private String reason;
    // For the URL of the vehicle registration document image
    private String registrationDocumentUrl;

    // For the approval status (true/false)
    private boolean approved;

}

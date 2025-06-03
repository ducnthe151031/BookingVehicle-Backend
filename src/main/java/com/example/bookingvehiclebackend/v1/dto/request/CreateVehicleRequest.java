package com.example.bookingvehiclebackend.v1.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateVehicleRequest {
    private String name; // tên xe
    private String location; // địa điểm xe đặt tại
    private Integer minimum_age; // Độ tuổi của xe
    private BigDecimal daily_price; // Giá thuê theo ngày
    private String type; // Loại nhiên liệu: Diesel, Gasoline, Electric, Hybrid, Unknown
    private String gearbox; // Manual hoặc Automatic
    private Integer seats; // Số ghe ngồi
    private MultipartFile[] image; // Ảnh đại diện của xe
    private String category;
    private String brand;
    private String licensePlate;
    private String description;

    public CreateVehicleRequest() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getMinimum_age() {
        return minimum_age;
    }

    public void setMinimum_age(Integer minimum_age) {
        this.minimum_age = minimum_age;
    }

    public BigDecimal getDaily_price() {
        return daily_price;
    }

    public void setDaily_price(BigDecimal daily_price) {
        this.daily_price = daily_price;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGearbox() {
        return gearbox;
    }

    public void setGearbox(String gearbox) {
        this.gearbox = gearbox;
    }



    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public MultipartFile[] getImage() {
        return image;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setImage(MultipartFile[] image) {
        this.image = image;
    }
}

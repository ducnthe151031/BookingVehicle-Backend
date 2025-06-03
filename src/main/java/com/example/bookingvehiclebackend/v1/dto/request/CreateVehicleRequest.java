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
    private BigDecimal deposit; // Tiền đặt cọc
    private String type; // Loại nhiên liệu: Diesel, Gasoline, Electric, Hybrid, Unknown
    private String gearbox; // Manual hoặc Automatic
    private boolean aircon; // có điều hòa hay ko
    private Integer seats; // Số ghe ngồi
    private Integer doors; // Số cửa xe
    private MultipartFile[] image; // Ảnh đại diện của xe

    public CreateVehicleRequest() {
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

    public BigDecimal getDeposit() {
        return deposit;
    }

    public void setDeposit(BigDecimal deposit) {
        this.deposit = deposit;
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

    public boolean isAircon() {
        return aircon;
    }

    public void setAircon(boolean aircon) {
        this.aircon = aircon;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    public Integer getDoors() {
        return doors;
    }

    public void setDoors(Integer doors) {
        this.doors = doors;
    }

    public MultipartFile[] getImage() {
        return image;
    }

    public void setImage(MultipartFile[] image) {
        this.image = image;
    }
}

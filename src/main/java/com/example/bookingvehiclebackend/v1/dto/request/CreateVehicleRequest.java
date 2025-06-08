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
    private String name;
    private String location;
    private Integer minimum_age;
    private BigDecimal daily_price;
    private String type;
    private String gearbox;
    private Integer seats;
    private MultipartFile[] image;
    private String category;
    private String brand;
    private String licensePlate;
    private String description;
}

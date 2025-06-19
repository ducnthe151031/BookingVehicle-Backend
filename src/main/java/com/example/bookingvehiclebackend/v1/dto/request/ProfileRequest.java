package com.example.bookingvehiclebackend.v1.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileRequest {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    // --- NEW PROPERTIES FOR IMAGE URLs ---
    private String citizenIdCardUrl;
    private String driverLicenseUrl;
}

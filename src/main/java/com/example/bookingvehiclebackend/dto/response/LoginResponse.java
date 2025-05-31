package com.example.bookingvehiclebackend.dto.response;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponse {
    private String token;
    private String refreshToken;

    public LoginResponse() {
    }

}

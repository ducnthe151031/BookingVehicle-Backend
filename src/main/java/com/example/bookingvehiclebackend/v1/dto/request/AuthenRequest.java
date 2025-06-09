package com.example.bookingvehiclebackend.v1.dto.request;

import com.example.bookingvehiclebackend.v1.dto.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenRequest {
    private String username;
    private String email;
    private String password;
    private Role role;
    private String oldPassword;
    private String newPassword;
}

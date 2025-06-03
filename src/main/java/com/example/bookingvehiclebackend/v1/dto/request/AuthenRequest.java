package com.example.bookingvehiclebackend.v1.dto.request;
import com.example.bookingvehiclebackend.v1.dto.Role;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AuthenRequest {
    private String username;
    private String email;
    private String password;
    private Role role;

    public AuthenRequest() {
    }

}

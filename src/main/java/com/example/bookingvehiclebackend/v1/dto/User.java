package com.example.bookingvehiclebackend.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;


@Entity
@Table(name = "user", schema = "vehicle_rental_system")
@Getter
@Setter
public class User implements  UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "username", length = 45)
    private String username;

    @Column(name = "password", length = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @ColumnDefault("'INACTIVE'")
    @Column(name = "flag_active", nullable = false, length = 100)
    private String flagActive;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(name = "phone_number", length = 100)
    private String phoneNumber;

    @Column(name = "avartar_url", length = 100)
    private String avartarUrl;

    @Column(name = "address", length = 100)
    private String address;
    
    @Column(name = "citizen_id_card_url", length = 255) // Column for Citizen ID Card image URL
    private String citizenIdCardUrl;

    @Column(name = "driver_license_url", length = 255) // Column for Driver's License image URL
    private String driverLicenseUrl;



    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
    @Override
    public String getUsername() {
        return username;
    }


    @Override
    public String getPassword() {
        return password;
    }


}
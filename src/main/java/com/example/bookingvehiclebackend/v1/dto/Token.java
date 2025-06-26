package com.example.bookingvehiclebackend.v1.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@Entity
@Table(name = "token", schema = "vehicle_rental_system")
@NoArgsConstructor
@AllArgsConstructor
public class Token {
    @Id
    @Column(name = "id", nullable = false, length = 36)
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "access_token", nullable = false, length = 1000)
    private String accessToken;

    @Column(name = "refresh_token", length = 1000)
    private String refreshToken;

    @ColumnDefault("b'0'")
    @Column(name = "revoked", nullable = false)
    private Boolean revoked = false;

    @ColumnDefault("b'0'")
    @Column(name = "expired", nullable = false)
    private Boolean expired = false;

    @Column(name = "token_type", length = 50)
    private String tokenType;
}
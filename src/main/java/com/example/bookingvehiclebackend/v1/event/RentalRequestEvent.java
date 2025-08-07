package com.example.bookingvehiclebackend.v1.event;

import com.example.bookingvehiclebackend.v1.dto.RentalRequest;
import com.example.bookingvehiclebackend.v1.dto.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RentalRequestEvent extends ApplicationEvent {
    private RentalRequest rentalRequest;
    private User user;
    private String jwtToken;


    public RentalRequestEvent(User user,RentalRequest rentalRequest, String jwtToken) {
        super(user);
        this.rentalRequest = rentalRequest;
        this.jwtToken = jwtToken;
    }
}

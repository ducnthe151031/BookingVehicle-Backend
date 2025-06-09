package com.example.bookingvehiclebackend.v1.event;

import com.example.bookingvehiclebackend.v1.dto.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {
    private User user;
    private String applicationUrl;
    private String jwtToken;

    public RegistrationCompleteEvent(User user, String applicationUrl, String jwtToken) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
        this.jwtToken = jwtToken;
    }
}

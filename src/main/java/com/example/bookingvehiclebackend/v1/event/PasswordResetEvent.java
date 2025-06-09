package com.example.bookingvehiclebackend.v1.event;

import com.example.bookingvehiclebackend.v1.dto.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class PasswordResetEvent extends ApplicationEvent {
    private User user;
    private String applicationUrl;
    private String resetToken;

    public PasswordResetEvent(User user, String applicationUrl, String resetToken) {
        super(user);
        this.user = user;
        this.applicationUrl = applicationUrl;
        this.resetToken = resetToken;
    }
}
package com.example.bookingvehiclebackend.service;

import com.example.bookingvehiclebackend.dto.User;

public interface UserService {
    User findByEmail(String email);
    void updatePassword(User user, String newPassword);
}

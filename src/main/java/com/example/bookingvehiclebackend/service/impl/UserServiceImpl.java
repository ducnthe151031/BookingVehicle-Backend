package com.example.bookingvehiclebackend.service.impl;

import com.example.bookingvehiclebackend.dto.User;
import com.example.bookingvehiclebackend.repository.UserRepository;
import com.example.bookingvehiclebackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Override
    public User findByEmail(String email) {
        return UserRepository .findByEmail(email);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

    }
}

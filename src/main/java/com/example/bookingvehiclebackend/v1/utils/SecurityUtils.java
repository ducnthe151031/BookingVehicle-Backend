package com.example.bookingvehiclebackend.v1.utils;

import com.example.bookingvehiclebackend.v1.dto.Role;
import com.example.bookingvehiclebackend.v1.dto.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

public class SecurityUtils {
    // Lấy thông tin User hiện tại nếu có
    public static Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails userDetails && userDetails instanceof User user) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    // Kiểm tra nếu user hiện tại có bất kỳ role nào trong danh sách truyền vào
    public static boolean hasRole(Role... roles) {
        return getCurrentUser()
                .map(user -> new HashSet<>(Arrays.asList(roles)).contains(user.getRole()))
                .orElse(false);
    }
}

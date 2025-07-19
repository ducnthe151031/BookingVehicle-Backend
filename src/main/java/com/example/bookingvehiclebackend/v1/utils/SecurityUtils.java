package com.example.bookingvehiclebackend.v1.utils;

import com.example.bookingvehiclebackend.v1.dto.Role;
import com.example.bookingvehiclebackend.v1.dto.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

@Slf4j
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

    public static String extractFrontendUrl(HttpServletRequest request) {
        // Thử lấy từ Origin header trước
        String origin = request.getHeader("Origin");
        if (origin != null && !origin.isEmpty()) {
            return origin;
        }

        // Nếu không có Origin, lấy từ Referer
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isEmpty()) {
            try {
                URL url = new URL(referer);
                StringBuilder baseUrl = new StringBuilder();
                baseUrl.append(url.getProtocol()).append("://").append(url.getHost());

                if (url.getPort() != -1) {
                    baseUrl.append(":").append(url.getPort());
                }

                return baseUrl.toString();
            } catch (Exception e) {
                // Log error và fallback
                log.warn("Cannot parse referer URL: {}", referer, e);
            }
        }

        // Fallback mặc định
        return "http://localhost:5173";
    }
}

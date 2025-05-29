package com.example.bookingvehiclebackend.service.impl;

import com.example.bookingvehiclebackend.config.JwtService;
import com.example.bookingvehiclebackend.dto.Token;
import com.example.bookingvehiclebackend.dto.User;
import com.example.bookingvehiclebackend.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.dto.response.LoginResponse;
import com.example.bookingvehiclebackend.exception.NhgClientException;
import com.example.bookingvehiclebackend.exception.NhgErrorHandler;
import com.example.bookingvehiclebackend.repository.TokenRepository;
import com.example.bookingvehiclebackend.repository.UserRepository;
import com.example.bookingvehiclebackend.service.AuthenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuthenServiceImpl implements AuthenService {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    JwtService jwtService;
    @Autowired
    TokenRepository tokenRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(AuthenRequest request) {
        // Authenticate the user with email and password
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(), request.getPassword()));
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User name not found"));

        if (!"ACTIVE".equals(user.getFlagActive())) {
            throw new RuntimeException("Account is not verified");
        }
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setRefreshToken(refreshToken);
        return loginResponse;
    }

    @Override
    public void saveUserToken(User user, String jwtToken) {
        Token token = new Token();
        token.setUser(user);
        token.setAccessToken(jwtToken);
        token.setTokenType("BEARER");
        token.setExpired(false);
        token.setRevoked(false);
        tokenRepository.save(token);
    }

    @Override
    public LoginResponse register(AuthenRequest request, HttpServletRequest httpServletRequest) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw NhgClientException.ofHandler(NhgErrorHandler.USER_IS_EXISTED);
        }
//        if(ObjectUtils.isEmpty(request.getEmail())){
//            throw new RuntimeException("Email is required");
//        }

        // Khi user dang ki chua xac thuc mail -> flagActive = INACTIVE
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(Instant.now());
        user.setFlagActive("ACTIVE");
//        user.setFlagActive("INACTIVE");
        user.setRole(request.getRole());
        userRepository.save(user);
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        // Sau khi dang ki thanh cong, can xac thuc qua email
//        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(httpSe-rvletRequest), jwtToken));
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setRefreshToken(refreshToken);
        return loginResponse;
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}

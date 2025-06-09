package com.example.bookingvehiclebackend.v1.service.impl;

import com.example.bookingvehiclebackend.config.JwtService;
import com.example.bookingvehiclebackend.v1.dto.Role;
import com.example.bookingvehiclebackend.v1.dto.Token;
import com.example.bookingvehiclebackend.v1.dto.User;
import com.example.bookingvehiclebackend.v1.dto.request.AuthenRequest;
import com.example.bookingvehiclebackend.v1.dto.response.LoginResponse;
import com.example.bookingvehiclebackend.v1.exception.PvrsClientException;
import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import com.example.bookingvehiclebackend.v1.repository.TokenRepository;
import com.example.bookingvehiclebackend.v1.repository.UserRepository;
import com.example.bookingvehiclebackend.v1.service.AuthenService;
import com.example.bookingvehiclebackend.v1.utils.SecurityUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import com.example.bookingvehiclebackend.v1.event.PasswordResetEvent;
import com.example.bookingvehiclebackend.v1.event.RegistrationCompleteEvent;
import com.example.bookingvehiclebackend.v1.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenServiceImpl implements AuthenService {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private final TokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final ApplicationEventPublisher publisher;

    @Override
    public LoginResponse login(AuthenRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    request.getUsername(), request.getPassword()));
        } catch (Exception ex) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.UNAUTHORIZED);
        }
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));

        if (!"ACTIVE".equals(user.getFlagActive())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.NOT_VERIFIED);
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
            throw PvrsClientException.ofHandler(PvrsErrorHandler.USER_IS_EXISTED);
        }
        if(ObjectUtils.isEmpty(request.getEmail())){
            throw PvrsClientException.ofHandler(PvrsErrorHandler.EMAIL_NOT_FOUND);
        }

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
//        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(httpServletRequest), jwtToken));
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwtToken);
        loginResponse.setRefreshToken(refreshToken);
        return loginResponse;
    }

    @Override
    public void changePassword(AuthenRequest request) {
        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.EMAIL_NOT_FOUND);
        }
        if (passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
        } else {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.INVALID_PASSWORD);
        }
    }

    @Override
    public LoginResponse forgotPassword(AuthenRequest request, HttpServletRequest httpServletRequest) {
        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
        if (!Objects.equals(user.getEmail(), request.getEmail())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.EMAIL_NOT_FOUND);
        }
        //Neu nhu dung email roi -> thu hoi token cua email hien tai
        revokeAllUserTokens(user);
        String newToken = jwtService.generateToken(user);
        String resetUrl = "http://localhost:5173/forgotPassword?token=";
        publisher.publishEvent(new PasswordResetEvent(user, resetUrl, newToken));
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(newToken);
        return loginResponse;
    }

    @Override
    public String verifyEmail(String token) {
        Token theToken = tokenRepository.findByAccessToken(token)
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.TOKEN_INVALID));
        if ("ACTIVE".equals(theToken.getUser().getFlagActive())) {
            throw PvrsClientException.ofHandler(PvrsErrorHandler.USER_IS_VERIFIED);
        }
        if (jwtService.isTokenValid(theToken.getAccessToken(), theToken.getUser())
                && "INACTIVE".equals(theToken.getUser().getFlagActive())) {
            theToken.getUser().setFlagActive("ACTIVE");
            userRepository.save(theToken.getUser());
        }
        return "Successful";
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

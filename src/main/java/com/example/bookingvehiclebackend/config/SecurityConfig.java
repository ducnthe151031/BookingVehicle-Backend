package com.example.bookingvehiclebackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;

    private final JwtAuthenticationFilter jwtAuthFilter;

    private final LogoutHandler logoutHandler;
    private static final String[] WHITE_LIST_URL = {
            "v1/auth/**",
    };

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(Customizer.withDefaults()) // Cho phép sử dụng cấu hình CORS từ WebMvcConfig
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers(WHITE_LIST_URL).permitAll();
                    // Cho phép cả OWNER và ADMIN truy cập POST /v1/admin/cars
                    auth.requestMatchers(HttpMethod.POST, "/v1/admin/cars").hasAnyRole("OWNER", "ADMIN");
                    auth.requestMatchers(HttpMethod.GET, "/v1/user/profile").hasAnyRole("OWNER", "ADMIN","USER");
                    auth.requestMatchers(HttpMethod.POST, "/v1/user/change-password").hasAnyRole("OWNER", "ADMIN","USER");
                    auth.requestMatchers(HttpMethod.POST, "/v1/user/forgot-password").hasAnyRole("OWNER", "ADMIN","USER");
                    auth.requestMatchers(HttpMethod.PUT, "/v1/user/profile").hasAnyRole("OWNER", "ADMIN","USER");
                    auth.requestMatchers(HttpMethod.GET, "/v1/user/verify-email").hasAnyRole("OWNER", "ADMIN","USER");
                    auth.requestMatchers("v1/admin/**").hasRole("ADMIN");
                    auth.requestMatchers("v1/user/**").hasRole("USER");
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"You do not have permission to access this resource.\"}");
                        })
                )
                .logout(logout -> logout
                        .logoutUrl("/v1/auth/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            SecurityContextHolder.clearContext();
                            response.setStatus(HttpStatus.OK.value());
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\": \"Logout successful\"}");
                        })
                )
                .build();
    }
}

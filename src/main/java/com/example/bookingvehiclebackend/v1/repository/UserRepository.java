package com.example.bookingvehiclebackend.v1.repository;

import com.example.bookingvehiclebackend.v1.dto.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailAndUsername(String email, String password);
}

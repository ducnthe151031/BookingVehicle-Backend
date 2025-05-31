package com.example.bookingvehiclebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BookingVehicleBackendApplication {

    public static void main(String[] args) {
        try {
            SpringApplication.run(BookingVehicleBackendApplication.class, args);
            System.out.println("Server is running successfully.");
        } catch (Exception e) {
            System.err.println("Failed to start the server.");
            e.printStackTrace(); // In ra lỗi chi tiết để tiện debug
        }
    }
}
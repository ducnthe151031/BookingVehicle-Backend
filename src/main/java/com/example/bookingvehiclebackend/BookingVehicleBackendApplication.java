package com.example.bookingvehiclebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
//@EnableScheduling
public class BookingVehicleBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingVehicleBackendApplication.class, args);
        System.out.println("Server is running");
    }
}



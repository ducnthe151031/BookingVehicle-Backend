package com.example.bookingvehiclebackend.exception;

public interface INhgErrorHandler {
    String getCode();

    String getMessage();

    default String getMessage(Object... args) {
        return this.getMessage();
    }
}

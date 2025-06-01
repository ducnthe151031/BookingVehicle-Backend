package com.example.bookingvehiclebackend.exception.model;

import com.example.bookingvehiclebackend.exception.PvrsErrorHandler;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NhgError {
    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;
    private NhgError(HttpStatus status, String errorCode, String errorMessage) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static NhgError valueOf(HttpStatus status, String errorCode, String errorMessage) {
        return new NhgError(status, errorCode, errorMessage);
    }

    public static NhgError http500Error() {
        return new NhgError(HttpStatus.BAD_REQUEST, PvrsErrorHandler.SYSTEM_ERROR.getCode(), PvrsErrorHandler.SYSTEM_ERROR.getMessage());
    }

}

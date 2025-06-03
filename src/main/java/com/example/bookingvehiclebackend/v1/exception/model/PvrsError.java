package com.example.bookingvehiclebackend.v1.exception.model;

import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PvrsError {
    private final HttpStatus status;
    private final String errorCode;
    private final String errorMessage;
    private PvrsError(HttpStatus status, String errorCode, String errorMessage) {
        this.status = status;
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public static PvrsError valueOf(HttpStatus status, String errorCode, String errorMessage) {
        return new PvrsError(status, errorCode, errorMessage);
    }

    public static PvrsError http500Error() {
        return new PvrsError(HttpStatus.BAD_REQUEST, PvrsErrorHandler.SYSTEM_ERROR.getCode(), PvrsErrorHandler.SYSTEM_ERROR.getMessage());
    }

}

package com.example.bookingvehiclebackend.v1.exception;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class PvrsClientException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;

    public PvrsClientException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public PvrsClientException(PvrsErrorHandler error) {
        this.errorCode = error.getCode();
        this.errorMessage = error.getMessage();
    }

    public static PvrsClientException ofHandler(IPvrsErrorHandler error) {
        return new PvrsClientException(error.getCode(), error.getMessage());
    }
    public static PvrsClientException ofHandler(IPvrsErrorHandler error, Object... errorDescArgs) {
        String message = error.getMessage();
        String errorMessage = String.format(message, errorDescArgs);
        return new PvrsClientException(error.getCode(), errorMessage);
    }
    public static Supplier<PvrsClientException> supplier(IPvrsErrorHandler error, Object... errorDescArgs) {
        return () -> ofHandler(error, errorDescArgs);
    }
    public static Supplier<PvrsClientException> supplier(IPvrsErrorHandler error) {
        return () -> ofHandler(error);
    }
    public static void assertTrue(boolean expression, PvrsErrorHandler error) {
        if (!expression) {
            throw new PvrsClientException(error);
        }
    }

}

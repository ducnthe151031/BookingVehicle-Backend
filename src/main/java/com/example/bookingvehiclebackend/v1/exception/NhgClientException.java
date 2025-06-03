package com.example.bookingvehiclebackend.v1.exception;
import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class NhgClientException extends RuntimeException {
    private final String errorCode;
    private final String errorMessage;

    public NhgClientException(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public NhgClientException(NhgErrorHandler error) {
        this.errorCode = error.getCode();
        this.errorMessage = error.getMessage();
    }

    public static NhgClientException ofHandler(INhgErrorHandler error) {
        return new NhgClientException(error.getCode(), error.getMessage());
    }
    public static NhgClientException ofHandler(INhgErrorHandler error, Object... errorDescArgs) {
        String message = error.getMessage();
        String errorMessage = String.format(message, errorDescArgs);
        return new NhgClientException(error.getCode(), errorMessage);
    }
    public static Supplier<NhgClientException> supplier(INhgErrorHandler error, Object... errorDescArgs) {
        return () -> ofHandler(error, errorDescArgs);
    }
    public static Supplier<NhgClientException> supplier(INhgErrorHandler error) {
        return () -> ofHandler(error);
    }
    public static void assertTrue(boolean expression, NhgErrorHandler error) {
        if (!expression) {
            throw new NhgClientException(error);
        }
    }

}

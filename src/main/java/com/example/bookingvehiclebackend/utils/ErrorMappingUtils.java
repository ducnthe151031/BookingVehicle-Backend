package com.example.bookingvehiclebackend.utils;

import com.example.bookingvehiclebackend.v1.exception.model.PvrsError;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ErrorMappingUtils {
    private static final Map<String, HttpStatus> errorMap = new HashMap<>();
    private ErrorMappingUtils() {
    }
    public static PvrsError getError(String code) {
        Optional<String> representativeCode = getRepresentativeCode(code);
        if (representativeCode.isEmpty()) {
            return PvrsError.http500Error();
        } else {
            HttpStatus httpStatus = errorMap.getOrDefault(representativeCode.get(), HttpStatus.BAD_REQUEST);
            return PvrsError.valueOf(httpStatus, code, httpStatus.getReasonPhrase());
        }
    }

    private static Optional<String> getRepresentativeCode(String code) {
        return StringUtils.hasText(code) && code.length() >= 3 ? Optional.of(code.substring(2, 3)) : Optional.empty();
    }

    static {
        errorMap.put("A", HttpStatus.BAD_REQUEST);
        errorMap.put("B", HttpStatus.UNAUTHORIZED);
        errorMap.put("C", HttpStatus.FORBIDDEN);
        errorMap.put("D", HttpStatus.NOT_FOUND);
        errorMap.put("E", HttpStatus.METHOD_NOT_ALLOWED);
        errorMap.put("F", HttpStatus.NOT_ACCEPTABLE);
        errorMap.put("G", HttpStatus.PROXY_AUTHENTICATION_REQUIRED);
        errorMap.put("H", HttpStatus.REQUEST_TIMEOUT);
        errorMap.put("I", HttpStatus.CONFLICT);
        errorMap.put("J", HttpStatus.LENGTH_REQUIRED);
        errorMap.put("K", HttpStatus.PRECONDITION_FAILED);
        errorMap.put("L", HttpStatus.PAYLOAD_TOO_LARGE);
        errorMap.put("M", HttpStatus.URI_TOO_LONG);
        errorMap.put("N", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        errorMap.put("O", HttpStatus.REQUESTED_RANGE_NOT_SATISFIABLE);
        errorMap.put("P", HttpStatus.EXPECTATION_FAILED);
        errorMap.put("Q", HttpStatus.UNPROCESSABLE_ENTITY);
        errorMap.put("R", HttpStatus.LOCKED);
        errorMap.put("S", HttpStatus.FAILED_DEPENDENCY);
        errorMap.put("T", HttpStatus.UPGRADE_REQUIRED);
        errorMap.put("U", HttpStatus.PRECONDITION_REQUIRED);
        errorMap.put("V", HttpStatus.TOO_MANY_REQUESTS);
    }
}

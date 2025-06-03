package com.example.bookingvehiclebackend.utils;


import com.example.bookingvehiclebackend.v1.exception.INhgErrorHandler;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseApiResponse<T> {
    private static final String SUCCEED_CODE = "MSG000000";
    private static final String SUCCEED_MESSAGE = "common.BaseApiResponse.success";
    private final Integer httpStatus;
    private final String code;
    private final String message;
    private final String clientMessageId;
    private final String path;
    private final T data;

    private BaseApiResponse(Integer httpStatus, String code, String message, T data) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
        this.clientMessageId = HttpUtils.getClientMessageId();
        this.path = HttpUtils.getServicePath();
        this.data = data;
    }

    public static <T> BaseApiResponse<T> succeed() {
        return succeed(null);
    }

    public static <T> BaseApiResponse<T> succeed(T data) {
        MessageUtils messageUtils = BeanUtils.getBean(MessageUtils.class);
        return new BaseApiResponse<>(HttpStatus.OK.value(), "MSG000000", messageUtils.getMessage("common.BaseApiResponse.success"), data);
    }

    public static <T> BaseApiResponse<T> failedOfBadRequest(INhgErrorHandler error) {
        return new BaseApiResponse<>(HttpStatus.BAD_REQUEST.value(), error.getCode(), error.getMessage(), null);
    }
    public static <T> BaseApiResponse<T> invalidCredentials(INhgErrorHandler error) {
        return new BaseApiResponse<>(HttpStatus.UNAUTHORIZED.value(), error.getCode(), error.getMessage(), null);
    }

}

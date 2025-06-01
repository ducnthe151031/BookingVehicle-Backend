package com.example.bookingvehiclebackend.exception;


import com.example.bookingvehiclebackend.utils.BeanUtils;
import com.example.bookingvehiclebackend.utils.MessageUtils;

public enum PvrsErrorHandler implements IPvrsErrorHandler {
    SYSTEM_ERROR("MEA0000011", "common.BaseApiResponse.systemError"),
    INVALID_INPUT("MEA000001", "common.apiResponse.invalidInput" ),
    UNAUTHORIZED("Unauthorized", "common.apiResponse.unauthorized"),
    LOGIN_ERROR("MEI009023", "Login error" ),
    USERNAME_NOT_FOUND("MEA000002", "common.apiResponse.usernameNotFound" ),
    TOKEN_INVALID("MEA000003", "common.apiResponse.tokenInvalid" ),
    USER_IS_EXISTED("MEA000004", "User already existed!" ),
    SEND_MAIL_ERROR("MEA000005", "common.apiResponse.sendMailError"),
    USER_IS_VERIFIED("MEA000006", "common.apiResponse.userIsVerified" ),
    INVALID_PASSWORD("MEA000007", "common.apiResponse.invalidPassword" ),
    INVALID_ROLE("MEA000008","common.apiResponse.invalidRole" ),
    EMAIL_NOT_FOUND("MEA000009", "common.apiResponse.emailNotFound"),
    NOT_VERIFIED("MEA000010", "common.apiResponse.notVerified"),
    USER_NOT_FOUND("MEA000011", "common.apiResponse.userNotFound" ),
    COURSE_NOT_FOUND("MEA000012", "common.apiResponse.courseNotFound"),
    COURSE_ALREADY_EXISTED("MEA000013", "common.apiResponse.courseAlreadyExisted" ),;
    private final String code;
    private final String message;

     PvrsErrorHandler(String code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMessage() {
        MessageUtils messageUtils = BeanUtils.getBean(MessageUtils.class);
        return messageUtils.getMessage(this.message);
    }
}

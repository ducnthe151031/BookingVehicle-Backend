package com.example.bookingvehiclebackend.v1.exception;


import com.example.bookingvehiclebackend.utils.BeanUtils;
import com.example.bookingvehiclebackend.utils.MessageUtils;

public enum PvrsErrorHandler implements IPvrsErrorHandler{
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
    EMAIL_NOT_FOUND("MEA000009", "common.apiResponse.emailNotFound"),
    NOT_VERIFIED("MEA000010", "common.apiResponse.notVerified"),
    USER_NOT_FOUND("MEA000011", "common.apiResponse.userNotFound" ),
    NOT_ALLOW_TO_CREATE_VEHICLE("MEA000014", "common.apiResponse.notAllowToCreateVehicle"),
    NOT_ALLOW_TO_BOOK_VEHICLE("MEA000015", "common.apiResponse.notAllowToBookVehicle"),
    VEHICLE_NOT_FOUND("MEA000016","common.apiResponse.vehicleNotFound"),
    VEHICLE_UNAVAILABLE("MEA000017","common.apiResponse.vehicleUnavailable"),
    VEHICLE_ALREADY_BOOKED("MEA000018","common.apiResponse.vehicleAlreadyBooked"),
    RENTAL_TIME_OVER_0_DAY("MEA000019","common.apiResponse.rentalTimeOver0Day"),
    BOOKING_IS_NOT_PENDING_STATUS("MEA000020", "common.apiResponse.bookingIsNotPendingStatus"),
    VEHICLE_EXISTED("MEA000021","common.apiResponse.vehicleExisted" ),
    RENTAL_REQUEST_NOT_COMPLETED("MEA000022", "Đơn thuê xe chưa hoàn thành, bạn không thể để lại đánh giá."),
    RENTAL_REQUEST_NOT_FOUND("MEA000023", "Không tìm thấy đơn thuê xe."),
    REVIEW_ALREADY_EXISTS("MEA000024", "Bạn đã đánh giá cho đơn thuê xe này rồi."),
    REVIEW_NOT_FOUND("MEA000025", "Không tìm thấy đánh giá."),
    NOT_REVIEW_AUTHOR("MEA000026", "Bạn không có quyền thực hiện hành động này với đánh giá của người khác."),
    INVALID_RATING("MEA000027", "Điểm đánh giá phải từ 1 đến 5."),
    COUPON_NOT_FOUND("MEA000028", "Coupon không tồn tại"),
    COUPON_IS_EXISTED("MEA000029", "Mã coupon đã tồn tại"),
    INVALID_PASSWORD_FORMAT("MEA000030", "Password must contain at least one uppercase letter, one digit, and one special character."),
    NOT_EMPTY_CCCD_AND_LICENSE("MEA000031","Vui lòng cập nhật thông tin CCCD và bằng lái xe"),
    NOT_YOUR_EMAIL("MEA0000032", "common.apiResponse.notYourEmail"),
    EMAIL_IS_EXISTED("MEA0000033", "Email already existed!" );


            ;
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

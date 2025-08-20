package com.example.bookingvehiclebackend.v1.dto;

import lombok.Getter;

@Getter
public enum DeliveryStatus {
    READY_TO_PICK("Chờ lấy xe"),
    TRANSIT("Đang giao xe"),
    DELIVERED("Đã nhận xe"),
    RETURNED("Đã trả xe"),
    CONFIRM_RETURNED("Xác nhận đã trả xe");
    private final String deliveryStatus;

    DeliveryStatus(String deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }
}

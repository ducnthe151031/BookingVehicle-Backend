package com.example.bookingvehiclebackend.v1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/payment")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/create")
    public BaseApiResponse<?> createPayment(@RequestBody PaymentRequest request) {
        return BaseApiResponse.succeed(paymentService.createPayment(request));
    }

    @GetMapping("/qr/{bookingId}")
    public BaseApiResponse<?> generateQrForBooking(@PathVariable String bookingId) {
        return BaseApiResponse.succeed(paymentService.generateQrCode(bookingId));
    }

    @GetMapping("/status/{bookingId}")
    public BaseApiResponse<?> getPaymentStatus(@PathVariable String bookingId) {
        return BaseApiResponse.succeed(paymentService.getPaymentStatus(bookingId));
    }
}

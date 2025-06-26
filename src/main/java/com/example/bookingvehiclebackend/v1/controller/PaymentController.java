package com.example.bookingvehiclebackend.v1.controller;


import com.example.bookingvehiclebackend.v1.dto.Payment;
import com.example.bookingvehiclebackend.v1.dto.request.QrRequest;
import com.example.bookingvehiclebackend.v1.service.impl.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/user")  // This is the correct way
public class PaymentController {
    private final PaymentService paymentService;

    @Autowired
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment-requests")
    public void initiatePayment(@RequestBody QrRequest qrRequest) throws Exception {

        paymentService.createPaymentRequest(qrRequest) ;
    }
    //
//    @GetMapping("/payment-requests")
//    public ResponseEntity<List<com.wrm.application.model.Payment>> getAllPayments() {
//        List<com.wrm.application.model.Payment> payments = paymentService.getAllPayments();
//        return ResponseEntity.ok(payments);
//    }
//
//
    @GetMapping("/payment-requests/user")
    public  List<Payment> getAllPaymentsByUser() throws Exception{
        return paymentService.getAllPaymentsByUser();
    }
//
//    @PutMapping("/payment-requests/{id}")
//    public ResponseEntity<com.wrm.application.model.Payment> updatePayment(@PathVariable Long id, @RequestBody com.wrm.application.model.Payment payment) {
//        com.wrm.application.model.Payment updatedPayment = paymentService.updatePayment(id, payment);
//        return ResponseEntity.ok(updatedPayment);
//    }
//
//    @DeleteMapping("/payment-requests/{id}")
//    public paymentService<Void> deletePayment(@PathVariable Long id) {
//        payOSPaymentService.deletePayment(id);
//        return ResponseEntity.noContent().build();
//    }
}
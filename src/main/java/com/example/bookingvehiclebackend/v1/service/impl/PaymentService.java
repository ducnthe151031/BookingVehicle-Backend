package com.example.bookingvehiclebackend.v1.service.impl;

import com.example.bookingvehiclebackend.v1.dto.Payment;
import com.example.bookingvehiclebackend.v1.dto.User;
import com.example.bookingvehiclebackend.v1.dto.request.QrRequest;
import com.example.bookingvehiclebackend.v1.exception.PvrsClientException;
import com.example.bookingvehiclebackend.v1.exception.PvrsErrorHandler;
import com.example.bookingvehiclebackend.v1.repository.PaymentRepository;
import com.example.bookingvehiclebackend.v1.repository.UserRepository;

import com.example.bookingvehiclebackend.v1.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import vn.payos.PayOS;
import vn.payos.type.PaymentData;

import java.util.List;

@RequiredArgsConstructor

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;


    public void createPaymentRequest(QrRequest qrRequest) throws Exception {
        PayOS payOS = new PayOS("65f3da88-9fa3-4ef3-8a5d-9393a14d3b84","c0473543-c34c-4601-8f23-88fc7fec2645","f3d3ea900cf6dd94e57b86b3c09ae3946ab397569ea3a930559a1c2a0797f25f") ;

        PaymentData paymentData = PaymentData.builder()
                .orderCode(qrRequest.getOrderCode())
                .amount(qrRequest.getAmount())
                .description(qrRequest.getDescription())
                .returnUrl(qrRequest.getReturnUrl())
                .cancelUrl(qrRequest.getCancelUrl())
                .build();
        User user = SecurityUtils.getCurrentUser()
                .orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
        Payment payment = new Payment();
        payment.setAmount(qrRequest.getAmount());
        payment.setBookingId(qrRequest.getBookingId());
        payment.setPayment(false);
        payment.setDescription(qrRequest.getDescription());
        payment.setUserId(user.getId());
        payment.setUrl(payOS.createPaymentLink(paymentData).getCheckoutUrl());
        paymentRepository.save(payment);


    }


    //    public List<Payment> getAllPayments() {
//        return paymentRepository.findAll();
//    }
//
    public List<Payment> getAllPaymentsByUser()  {
        User user = SecurityUtils.getCurrentUser().orElseThrow(PvrsClientException.supplier(PvrsErrorHandler.UNAUTHORIZED));
        return paymentRepository.findByUserId(user.getId());


    }
////
////    public Payment updatePayment(Long id, Payment paymentDetails) {
////        Payment payment = paymentRepository.findById(id)
////                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found with id: " + id));
////
////        payment.set_payment(paymentDetails.is_payment());
////
////        return paymentRepository.save(payment);
////    }
////
////    public void deletePayment(Long id) {
////        Payment payment = paymentRepository.findById(id)
////                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found with id: " + id));
////
////        paymentRepository.delete(payment);
////    }

}
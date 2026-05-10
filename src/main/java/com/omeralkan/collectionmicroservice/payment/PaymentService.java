package com.omeralkan.collectionmicroservice.payment;

public interface PaymentService {
    PaymentResponseDto processPayment(PaymentRequestDto request);
}
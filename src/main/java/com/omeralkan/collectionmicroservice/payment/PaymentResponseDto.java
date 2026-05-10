package com.omeralkan.collectionmicroservice.payment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    private boolean success;
    private String transactionId;
    private String message;
}
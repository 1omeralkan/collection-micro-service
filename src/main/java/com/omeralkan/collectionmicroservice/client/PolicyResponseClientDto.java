package com.omeralkan.collectionmicroservice.client;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class PolicyResponseClientDto {
    private Long id;
    private Long productId;
    private BigDecimal amount;
    private String currencyCode;
    private String policyStatus;
}
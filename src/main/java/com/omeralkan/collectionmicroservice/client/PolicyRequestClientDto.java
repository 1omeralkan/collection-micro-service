package com.omeralkan.collectionmicroservice.client;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
public class PolicyRequestClientDto {
    private Long productId;
    private BigDecimal amount;
    private String currencyCode;
    private LocalDate startDate;
    private LocalDate endDate;
}
package com.omeralkan.collectionmicroservice.client;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ApplicationResponseClientDto {

    private Long id;
    private String applicationNumber;
    private Long customerId;
    private Long productId;
    private Long productAmountId;
    private BigDecimal amount;
    private LocalDate applicationDate;
    private String status;
    private String paymentTypeCode;
    private Integer installmentCount;
    private Boolean isActive;

    private String currencyCode;
}
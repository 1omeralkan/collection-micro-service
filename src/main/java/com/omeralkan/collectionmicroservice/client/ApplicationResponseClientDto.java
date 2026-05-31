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
    private String customerName;
    private Long productId;
    private String productName;
    private Long productAmountId;
    private BigDecimal amount;
    private LocalDate applicationDate;
    private String status;
    private String description;
    private String paymentTypeCode;
    private String paymentTypeName;
    private Integer installmentCount;
    private BigDecimal installmentAmount;
    private Boolean isActive;
    private String currencyCode;
}
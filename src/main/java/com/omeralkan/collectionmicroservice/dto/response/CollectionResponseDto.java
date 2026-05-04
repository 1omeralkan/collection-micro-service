package com.omeralkan.collectionmicroservice.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class CollectionResponseDto {

    private Long id;
    private Long applicationId;
    private Long policyId;
    private Integer installmentNumber;
    private BigDecimal installmentAmount;
    private LocalDate dueDate;
    private Boolean isActive;

    private Boolean isPaid;
}
package com.omeralkan.collectionmicroservice.service;

import com.omeralkan.collectionmicroservice.dto.response.CollectionResponseDto;
import com.omeralkan.collectionmicroservice.payment.PaymentRequestDto;

import java.util.List;

public interface CollectionService {

    List<CollectionResponseDto> createCollectionsByApplicationId(Long applicationId);

    List<CollectionResponseDto> getAllCollections();

    CollectionResponseDto getCollectionById(Long id);

    List<CollectionResponseDto> getCollectionsByApplicationId(Long applicationId);

    CollectionResponseDto payInstallment(Long id, PaymentRequestDto paymentRequest);

    void deleteCollection(Long id);
}
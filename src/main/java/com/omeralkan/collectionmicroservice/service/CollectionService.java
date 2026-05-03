package com.omeralkan.collectionmicroservice.service;

import com.omeralkan.collectionmicroservice.dto.response.CollectionResponseDto;

import java.util.List;

public interface CollectionService {

    List<CollectionResponseDto> createCollectionsByApplicationId(Long applicationId);

    List<CollectionResponseDto> getAllCollections();

    CollectionResponseDto getCollectionById(Long id);

    List<CollectionResponseDto> getCollectionsByApplicationId(Long applicationId);

    CollectionResponseDto payInstallment(Long id);

    void deleteCollection(Long id);
}
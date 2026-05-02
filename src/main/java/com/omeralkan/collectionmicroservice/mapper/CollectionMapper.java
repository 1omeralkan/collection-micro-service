package com.omeralkan.collectionmicroservice.mapper;

import com.omeralkan.collectionmicroservice.dto.response.CollectionResponseDto;
import com.omeralkan.collectionmicroservice.entity.CollectionEntity;
import org.springframework.stereotype.Component;

@Component
public class CollectionMapper {

    public CollectionResponseDto toResponse(CollectionEntity entity) {
        CollectionResponseDto dto = new CollectionResponseDto();
        dto.setId(entity.getId());
        dto.setApplicationId(entity.getApplicationId());
        dto.setPolicyId(entity.getPolicyId());
        dto.setInstallmentNumber(entity.getInstallmentNumber());
        dto.setInstallmentAmount(entity.getInstallmentAmount());
        dto.setDueDate(entity.getDueDate());
        dto.setStatus(entity.getStatus().name());
        dto.setIsActive(entity.getIsActive());
        return dto;
    }
}
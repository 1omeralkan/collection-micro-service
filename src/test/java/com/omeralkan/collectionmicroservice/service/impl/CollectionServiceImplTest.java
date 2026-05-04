package com.omeralkan.collectionmicroservice.service.impl;

import com.omeralkan.collectionmicroservice.client.ApplicationResponseClientDto;
import com.omeralkan.collectionmicroservice.client.ApplicationServiceClient;
import com.omeralkan.collectionmicroservice.dto.response.CollectionResponseDto;
import com.omeralkan.collectionmicroservice.entity.CollectionEntity;
import com.omeralkan.collectionmicroservice.exception.BusinessException;
import com.omeralkan.collectionmicroservice.mapper.CollectionMapper;
import com.omeralkan.collectionmicroservice.repository.CollectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CollectionServiceImplTest {

    @Mock
    private CollectionRepository collectionRepository;

    @Mock
    private CollectionMapper collectionMapper;

    @Mock
    private ApplicationServiceClient applicationServiceClient;

    @InjectMocks
    private CollectionServiceImpl collectionService;

    @Test
    void createCollections_WhenCashPayment_ShouldCreateOneRow() {
        ApplicationResponseClientDto application = new ApplicationResponseClientDto();
        application.setId(1L);
        application.setAmount(new BigDecimal("9000"));
        application.setApplicationDate(LocalDate.of(2026, 5, 1));
        application.setPaymentTypeCode("P");
        application.setInstallmentCount(1);

        when(applicationServiceClient.getApplicationById(1L)).thenReturn(application);
        when(collectionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(collectionMapper.toResponse(any())).thenReturn(new CollectionResponseDto());

        List<CollectionResponseDto> result = collectionService.createCollectionsByApplicationId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(collectionRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createCollections_WhenInstallmentPayment_ShouldCreateMultipleRows() {
        ApplicationResponseClientDto application = new ApplicationResponseClientDto();
        application.setId(1L);
        application.setAmount(new BigDecimal("9000"));
        application.setApplicationDate(LocalDate.of(2026, 5, 1));
        application.setPaymentTypeCode("T");
        application.setInstallmentCount(6);

        when(applicationServiceClient.getApplicationById(1L)).thenReturn(application);
        when(collectionRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));
        when(collectionMapper.toResponse(any())).thenReturn(new CollectionResponseDto());

        List<CollectionResponseDto> result = collectionService.createCollectionsByApplicationId(1L);

        assertNotNull(result);
        assertEquals(6, result.size());
        verify(collectionRepository, times(1)).saveAll(anyList());
    }

    @Test
    void createCollections_WhenApplicationServiceFails_ShouldThrowBusinessException() {
        when(applicationServiceClient.getApplicationById(999L))
                .thenThrow(new RuntimeException("Connection refused"));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            collectionService.createCollectionsByApplicationId(999L);
        });

        assertEquals("COL-APP-ERR", exception.getMessage());
        verify(collectionRepository, never()).saveAll(anyList());
    }

    @Test
    void payInstallment_ShouldChangeStatusToPaid() {
        CollectionEntity entity = new CollectionEntity();
        entity.setId(1L);
        entity.setIsPaid(false); // Güncellendi
        entity.setIsActive(true);

        when(collectionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(entity));
        when(collectionRepository.save(any())).thenReturn(entity);
        when(collectionMapper.toResponse(any())).thenReturn(new CollectionResponseDto());

        collectionService.payInstallment(1L);

        assertTrue(entity.getIsPaid()); // Güncellendi
        verify(collectionRepository, times(1)).save(entity);
    }

    @Test
    void payInstallment_WhenAlreadyPaid_ShouldThrowBusinessException() {
        CollectionEntity entity = new CollectionEntity();
        entity.setId(1L);
        entity.setIsPaid(true);
        entity.setIsActive(true);

        when(collectionRepository.findByIdAndIsActiveTrue(1L)).thenReturn(Optional.of(entity));

        BusinessException exception = assertThrows(BusinessException.class, () -> {
            collectionService.payInstallment(1L);
        });

        assertEquals("COL-400-PAID", exception.getMessage());
        verify(collectionRepository, never()).save(any());
    }
}
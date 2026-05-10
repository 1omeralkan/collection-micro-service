package com.omeralkan.collectionmicroservice.service.impl;

import com.omeralkan.collectionmicroservice.client.ApplicationResponseClientDto;
import com.omeralkan.collectionmicroservice.client.ApplicationServiceClient;
import com.omeralkan.collectionmicroservice.client.PolicyRequestClientDto; // EKLENDİ
import com.omeralkan.collectionmicroservice.client.PolicyResponseClientDto; // EKLENDİ
import com.omeralkan.collectionmicroservice.client.PolicyServiceClient; // EKLENDİ
import com.omeralkan.collectionmicroservice.dto.response.CollectionResponseDto;
import com.omeralkan.collectionmicroservice.entity.CollectionEntity;
import com.omeralkan.collectionmicroservice.exception.BusinessException;
import com.omeralkan.collectionmicroservice.exception.ErrorCodes;
import com.omeralkan.collectionmicroservice.mapper.CollectionMapper;
import com.omeralkan.collectionmicroservice.repository.CollectionRepository;
import com.omeralkan.collectionmicroservice.service.CollectionService;
import com.omeralkan.collectionmicroservice.payment.PaymentService;
import com.omeralkan.collectionmicroservice.payment.PaymentRequestDto;
import com.omeralkan.collectionmicroservice.payment.PaymentResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectionServiceImpl implements CollectionService {

    private final CollectionRepository collectionRepository;
    private final CollectionMapper collectionMapper;
    private final ApplicationServiceClient applicationServiceClient;
    private final PolicyServiceClient policyServiceClient;
    private final PaymentService paymentService;

    private static final String PAYMENT_TYPE_CASH = "P";

    @Override
    @Transactional
    public List<CollectionResponseDto> createCollectionsByApplicationId(Long applicationId) {

        ApplicationResponseClientDto application = getApplicationOrThrow(applicationId);

        List<CollectionEntity> collectionsToSave = buildCollections(application);

        List<CollectionEntity> savedCollections = collectionRepository.saveAll(collectionsToSave);

        log.info("Tahsilat oluşturuldu. ApplicationId: {}, Toplam Satır: {}, Ödeme Tipi: {}",
                applicationId, savedCollections.size(), application.getPaymentTypeCode());

        return savedCollections.stream()
                .map(collectionMapper::toResponse)
                .toList();
    }

    @Override
    public List<CollectionResponseDto> getAllCollections() {
        return collectionRepository.findAllByIsActiveTrue()
                .stream()
                .map(collectionMapper::toResponse)
                .toList();
    }

    @Override
    public CollectionResponseDto getCollectionById(Long id) {
        CollectionEntity entity = findActiveCollectionOrThrow(id);
        return collectionMapper.toResponse(entity);
    }

    @Override
    public List<CollectionResponseDto> getCollectionsByApplicationId(Long applicationId) {
        return collectionRepository.findAllByApplicationIdAndIsActiveTrue(applicationId)
                .stream()
                .map(collectionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CollectionResponseDto payInstallment(Long id, PaymentRequestDto paymentRequest) {
        CollectionEntity entity = findActiveCollectionOrThrow(id);

        if (Boolean.TRUE.equals(entity.getIsPaid())) {
            throw new BusinessException(ErrorCodes.COLLECTION_ALREADY_PAID, HttpStatus.BAD_REQUEST);
        }

        boolean hasUnpaidPastDebt = collectionRepository
                .existsByApplicationIdAndIsPaidFalseAndIsActiveTrueAndInstallmentNumberLessThan(
                        entity.getApplicationId(),
                        entity.getInstallmentNumber()
                );

        if (hasUnpaidPastDebt) {
            log.warn("Ödeme reddedildi. Geçmiş borç bulundu. ApplicationId: {}, İstenen Taksit: {}",
                    entity.getApplicationId(), entity.getInstallmentNumber());
            throw new BusinessException(ErrorCodes.COLLECTION_ALREADY_PAID, HttpStatus.BAD_REQUEST);
        }

        log.info("Collection ID: {} için ödeme servisi çağrılıyor...", id);
        PaymentResponseDto paymentResponse = paymentService.processPayment(paymentRequest);

        if (!paymentResponse.isSuccess()) {
            log.error("Ödeme alınamadı. Hata: {}", paymentResponse.getMessage());
            throw new RuntimeException("Ödeme işlemi başarısız: " + paymentResponse.getMessage());
        }

        entity.setIsPaid(true);

        if (entity.getInstallmentNumber() == 1 && entity.getPolicyId() == null) {

            ApplicationResponseClientDto application = getApplicationOrThrow(entity.getApplicationId());

            PolicyRequestClientDto policyRequest = PolicyRequestClientDto.builder()
                    .productId(application.getProductId())
                    .amount(application.getAmount())
                    .currencyCode(application.getCurrencyCode() != null ? application.getCurrencyCode() : "TRY")
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusYears(1)) // Standart 1 yıllık poliçe süresi
                    .build();

            Long realPolicyId;
            try {
                log.info("İlk taksit ödendi. Policy servisi tetikleniyor... ApplicationId: {}", application.getId());

                PolicyResponseClientDto policyResponse = policyServiceClient.createPolicy(policyRequest);
                realPolicyId = policyResponse.getId();

                log.info("Poliçe başarıyla oluşturuldu! Gerçek Policy ID: {}", realPolicyId);

            } catch (Exception e) {
                log.error("Policy servisi çağrılırken hata oluştu. Hata: {}", e.getMessage());
                throw new BusinessException("COL-POL-ERR", HttpStatus.SERVICE_UNAVAILABLE);
            }

            entity.setPolicyId(realPolicyId);

            List<CollectionEntity> allInstallments = collectionRepository
                    .findAllByApplicationIdAndIsActiveTrue(entity.getApplicationId());

            for (CollectionEntity installment : allInstallments) {
                if (installment.getPolicyId() == null && !installment.getId().equals(entity.getId())) {
                    installment.setPolicyId(realPolicyId);
                }
            }
            collectionRepository.saveAll(allInstallments);
        }

        CollectionEntity updatedEntity = collectionRepository.save(entity);

        log.info("Taksit ödendi. ID: {}, Taksit No: {}, Tutar: {}",
                id, entity.getInstallmentNumber(), entity.getInstallmentAmount());

        return collectionMapper.toResponse(updatedEntity);
    }

    @Override
    public void deleteCollection(Long id) {
        CollectionEntity entity = findActiveCollectionOrThrow(id);
        entity.setIsActive(false);
        collectionRepository.save(entity);

        log.info("Tahsilat kaydı silindi. ID: {}", id);
    }

    private CollectionEntity findActiveCollectionOrThrow(Long id) {
        return collectionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCodes.COLLECTION_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    private ApplicationResponseClientDto getApplicationOrThrow(Long applicationId) {
        try {
            return applicationServiceClient.getApplicationById(applicationId);
        } catch (Exception e) {
            log.error("Application servisi hatası. ApplicationId: {}, Hata: {}",
                    applicationId, e.getMessage());
            throw new BusinessException(
                    ErrorCodes.APPLICATION_SERVICE_ERROR, HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    private List<CollectionEntity> buildCollections(ApplicationResponseClientDto application) {
        List<CollectionEntity> collections = new ArrayList<>();

        if (PAYMENT_TYPE_CASH.equalsIgnoreCase(application.getPaymentTypeCode())) {
            collections.add(buildCollection(
                    application.getId(),
                    1,
                    application.getAmount(),
                    application.getApplicationDate()
            ));
            return collections;
        }

        int count = application.getInstallmentCount();
        BigDecimal installmentAmount = application.getAmount()
                .divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        for (int i = 0; i < count; i++) {
            collections.add(buildCollection(
                    application.getId(),
                    i + 1,
                    installmentAmount,
                    application.getApplicationDate().plusMonths(i + 1)
            ));
        }

        return collections;
    }

    private CollectionEntity buildCollection(Long applicationId, Integer installmentNumber,
                                             BigDecimal amount, LocalDate dueDate) {
        CollectionEntity entity = new CollectionEntity();
        entity.setApplicationId(applicationId);
        entity.setPolicyId(null);
        entity.setInstallmentNumber(installmentNumber);
        entity.setInstallmentAmount(amount);
        entity.setDueDate(dueDate);
        entity.setIsPaid(false);

        return entity;
    }
}
package com.omeralkan.collectionmicroservice.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.UUID;

@Service
@Slf4j
public class MockPaymentServiceImpl implements PaymentService {

    @Override
    public PaymentResponseDto processPayment(PaymentRequestDto request) {
        String maskedCard = "**** **** **** " + request.getCardNumber().substring(12);
        log.info("Ödeme provizyonu alınıyor... Kart: {}, Tutar: {}", maskedCard, request.getAmount());

        int year = Integer.parseInt("20" + request.getExpireYear());
        int month = Integer.parseInt(request.getExpireMonth());

        if (YearMonth.of(year, month).isBefore(YearMonth.now())) {
            log.warn("Ödeme reddedildi. Sebep: Kartın son kullanma tarihi geçmiş.");
            return buildResponse(false, "Ödeme başarısız: Kartın son kullanma tarihi geçmiş.");
        }

        if (request.getCardNumber().startsWith("4000")) {
            log.warn("Ödeme reddedildi. Sebep: Yetersiz bakiye simülasyonu.");
            return buildResponse(false, "Banka reddi: Yetersiz bakiye.");
        }

        log.info("Ödeme başarıyla tahsil edildi.");
        return buildResponse(true, "İşlem başarılı.");
    }

    private PaymentResponseDto buildResponse(boolean success, String message) {
        return PaymentResponseDto.builder()
                .success(success)
                .transactionId(success ? UUID.randomUUID().toString().toUpperCase() : null)
                .message(message)
                .build();
    }
}
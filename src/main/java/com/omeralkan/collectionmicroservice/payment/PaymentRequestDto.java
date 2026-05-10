package com.omeralkan.collectionmicroservice.payment;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PaymentRequestDto {

    @LuhnCheck
    private String cardNumber;

    @NotBlank(message = "Kart sahibi adı boş olamaz")
    @Pattern(regexp = "^[a-zA-ZçÇğĞıİöÖşŞüÜ\\s]{5,50}$", message = "İsim sadece harf içermeli ve 5-50 karakter olmalı")
    private String cardHolderName;

    @NotBlank(message = "Ay boş olamaz")
    @Pattern(regexp = "^(0[1-9]|1[0-2])$", message = "Ay 01-12 arasında olmalı")
    private String expireMonth;

    @NotBlank(message = "Yıl boş olamaz")
    @Pattern(regexp = "^(2[4-9]|[3-9][0-9])$", message = "Geçersiz yıl formatı (Minimum 24 olmalı)")
    private String expireYear;

    @NotBlank(message = "CVV boş olamaz")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV 3 veya 4 haneli rakam olmalı")
    private String cvv;

    @NotNull(message = "Ödeme tutarı boş olamaz")
    @Positive(message = "Tutar 0'dan büyük olmalıdır")
    private BigDecimal amount;
}
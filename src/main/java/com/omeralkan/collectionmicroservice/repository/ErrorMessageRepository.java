package com.omeralkan.collectionmicroservice.repository;

import com.omeralkan.collectionmicroservice.entity.ErrorMessageEntity;
import com.omeralkan.collectionmicroservice.entity.ErrorMessageId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ErrorMessageRepository extends JpaRepository<ErrorMessageEntity, ErrorMessageId> {

    Optional<ErrorMessageEntity> findByErrorCodeAndLanguage(String errorCode, String language);
}
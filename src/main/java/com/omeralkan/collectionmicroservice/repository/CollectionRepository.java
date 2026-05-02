package com.omeralkan.collectionmicroservice.repository;
import com.omeralkan.collectionmicroservice.entity.CollectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CollectionRepository extends JpaRepository<CollectionEntity, Long> {

    List<CollectionEntity> findAllByIsActiveTrue();

    List<CollectionEntity> findAllByApplicationIdAndIsActiveTrue(Long applicationId);

    Optional<CollectionEntity> findByIdAndIsActiveTrue(Long id);
}
package com.omeralkan.collectionmicroservice.controller;

import com.omeralkan.collectionmicroservice.dto.response.CollectionResponseDto;
import com.omeralkan.collectionmicroservice.service.CollectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/collections")
@RequiredArgsConstructor
public class CollectionController {

    private final CollectionService collectionService;

    // Application servisi bu endpointi Feign ile çağıracak
    @PostMapping("/application/{applicationId}")
    public ResponseEntity<List<CollectionResponseDto>> createCollections(
            @PathVariable Long applicationId) {
        List<CollectionResponseDto> responses =
                collectionService.createCollectionsByApplicationId(applicationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping
    public ResponseEntity<List<CollectionResponseDto>> getAllCollections() {
        return ResponseEntity.ok(collectionService.getAllCollections());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CollectionResponseDto> getCollectionById(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.getCollectionById(id));
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<CollectionResponseDto>> getCollectionsByApplicationId(
            @PathVariable Long applicationId) {
        return ResponseEntity.ok(collectionService.getCollectionsByApplicationId(applicationId));
    }

    @PatchMapping("/{id}/pay")
    public ResponseEntity<CollectionResponseDto> payInstallment(@PathVariable Long id) {
        return ResponseEntity.ok(collectionService.payInstallment(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable Long id) {
        collectionService.deleteCollection(id);
        return ResponseEntity.noContent().build();
    }
}
package com.omeralkan.collectionmicroservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "application-service", url = "${application.service.url}")
public interface ApplicationServiceClient {

    @GetMapping("/api/v1/applications/{id}")
    ApplicationResponseClientDto getApplicationById(@PathVariable Long id);
}
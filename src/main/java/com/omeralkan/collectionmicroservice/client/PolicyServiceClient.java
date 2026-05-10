package com.omeralkan.collectionmicroservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "policy-service", url = "${policy.service.url}")
public interface PolicyServiceClient {

    @PostMapping("/api/v1/policies")
    PolicyResponseClientDto createPolicy(@RequestBody PolicyRequestClientDto requestDto);
}
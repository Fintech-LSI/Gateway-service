package com.fintech.gateway.Service.FeignClient;

import com.fintech.gateway.DTO.TokenRequest;
import com.fintech.gateway.DTO.ValidResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "http://auth-service:8946")
public interface AuthServiceClient {
  @PostMapping("/api/auth/validate-token")
  ValidResponse validateToken(@RequestBody TokenRequest tokenRequest);
}


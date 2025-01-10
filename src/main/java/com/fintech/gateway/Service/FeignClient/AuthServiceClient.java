package com.fintech.gateway.Service.FeignClient;

import com.fintech.gateway.DTO.TokenRequest;
import com.fintech.gateway.DTO.ValidResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "AUTH-SERVICE" , configuration = FeignClientProperties.FeignClientConfiguration.class)
public interface AuthServiceClient {
  @PostMapping("/api/auth/validate-token")
  ValidResponse validateToken(@RequestBody TokenRequest tokenRequest);
}


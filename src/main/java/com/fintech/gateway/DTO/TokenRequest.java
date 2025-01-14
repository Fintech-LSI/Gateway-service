package com.fintech.gateway.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenRequest {
    String token;
}

package com.fintech.gateway.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidResponse {
  Boolean valid ;
  String email ;
  String Role ;
  UserResponse user;

}

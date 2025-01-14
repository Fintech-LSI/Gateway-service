package com.fintech.gateway.DTO;

public record UserResponse(
  Long id,
  String firstName,
  String lastName,
  String email,
  String role
) { }

package com.fintech.gateway.Config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintech.gateway.DTO.TokenRequest;
import com.fintech.gateway.DTO.ValidResponse;
import com.fintech.gateway.Service.FeignClient.AuthServiceClient;
import io.micrometer.common.util.internal.logging.InternalLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class JwtAuthenticationFilter implements WebFilter {  // Changed from extending OncePerRequestFilter

  private final AuthServiceClient authServiceClient;
  @Value("#{'${var.filter.excluded-paths}'.split(',')}")
  private  List<String> EXCLUDED_PATHS ;

  public JwtAuthenticationFilter(AuthServiceClient authServiceClient) {
    this.authServiceClient = authServiceClient;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();
    String path = request.getPath().value();

    for (String excludedPath : EXCLUDED_PATHS) {
      if (path.startsWith(excludedPath)) {
        return chain.filter(exchange);
      }
    }
    String token = extractToken(exchange);
    if (token == null || token.isEmpty()) {
      return respondWithStatus(exchange, HttpStatus.UNAUTHORIZED, "Missing authentication token.");
    }

    return Mono.fromCallable(() -> authServiceClient.validateToken(TokenRequest.builder().token(token).build()))
      .subscribeOn(Schedulers.boundedElastic()) // Move blocking operation to a separate thread pool
      .flatMap(response -> {
        if (!Boolean.TRUE.equals(response.getValid())) {
          return respondWithStatus(exchange, HttpStatus.UNAUTHORIZED, "Invalid authentication token.");
        }

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + response.getRole()));
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(response.getEmail(), null, authorities);

        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
      })
      .onErrorResume(e -> {
        return respondWithStatus(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "Error during authentication ::"+e.getMessage());
      });
  }

  private String extractToken(ServerWebExchange exchange) {
    String authHeader = exchange.getRequest()
      .getHeaders()
      .getFirst("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }

//  private Mono<Void> respondWithStatus(ServerWebExchange exchange, HttpStatus status, String message) {
//    exchange.getResponse().setStatusCode(status);
//    return exchange.getResponse().setComplete();
//  }
private Mono<Void> respondWithStatus(ServerWebExchange exchange, HttpStatus status, String message) {
   ObjectMapper objectMapper = new ObjectMapper();

  exchange.getResponse().setStatusCode(status);
  exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

  // Create JSON response
  String jsonResponse;
  try {
    jsonResponse = objectMapper.writeValueAsString(Map.of("error", message));
  } catch (Exception e) {
    jsonResponse = "{\"error\":\"Failed to create JSON response\"}";
  }

  // Write JSON response
  byte[] bytes = jsonResponse.getBytes();
  DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
  return exchange.getResponse().writeWith(Mono.just(buffer));
}


}

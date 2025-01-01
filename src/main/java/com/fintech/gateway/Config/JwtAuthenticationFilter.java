package com.fintech.gateway.Config;


import com.fintech.gateway.DTO.ValidResponse;
import com.fintech.gateway.Service.FeignClient.AuthServiceClient;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;

@Component
public class JwtAuthenticationFilter implements WebFilter {  // Changed from extending OncePerRequestFilter

  private final AuthServiceClient authServiceClient;

  public JwtAuthenticationFilter(AuthServiceClient authServiceClient) {
    this.authServiceClient = authServiceClient;
  }

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String token = extractToken(exchange);

    if (token == null) {
      exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
      return exchange.getResponse().setComplete();
    }

    /*
    * NOT NEEDED
    */
//
//    String path = exchange.getRequest().getPath().toString();
//    // Allow public paths to proceed without a token
//    if (isPublicPath(path)) {
//      return chain.filter(exchange);
//    }


    try {
      // Your token validation logic here
      ValidResponse response =  authServiceClient.validateToken(token);
      if (response.getValid().equals(Boolean.FALSE)) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
      }

      List<SimpleGrantedAuthority> authorities =
        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + response.getRole()));

      UsernamePasswordAuthenticationToken auth =
        new UsernamePasswordAuthenticationToken(response.getEmail(), null, authorities);

      return chain.filter(exchange)
        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));

    } catch (Exception e) {
      return Mono.error(e);
    }
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

}

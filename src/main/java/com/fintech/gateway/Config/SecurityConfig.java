package com.fintech.gateway.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthFilter;

  @Value("#{'${var.filter.excluded-paths}'.split(',')}")
  private List<String> excludeUrls;
  public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter) {
    this.jwtAuthFilter = jwtAuthFilter;
  }


  @Bean
  public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
    return http
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      .authorizeExchange(exchanges -> exchanges
        .pathMatchers(excludeUrls.toArray(new String[0])).permitAll()
        .anyExchange().authenticated()
      )
      .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
      .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
      .build();
  }
}

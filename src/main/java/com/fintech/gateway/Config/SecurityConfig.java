package com.fintech.gateway.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Collections;
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
    String[] patterns = excludeUrls.stream()
      .map(url -> url.endsWith("/**") ? url : url + "/**")
      .toArray(String[]::new);

    return http
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      .authorizeExchange(exchanges -> exchanges
        .pathMatchers(patterns).permitAll()
        .anyExchange().authenticated()
      )
      .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
      .addFilterAt(jwtAuthFilter, SecurityWebFiltersOrder.AUTHENTICATION)
      .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration corsConfiguration = new CorsConfiguration();
    corsConfiguration.setAllowedOrigins(Collections.singletonList("http://angular-frontend"));
    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
    corsConfiguration.setAllowedHeaders(Collections.singletonList("*"));
    corsConfiguration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", corsConfiguration);
    return source;
  }
}

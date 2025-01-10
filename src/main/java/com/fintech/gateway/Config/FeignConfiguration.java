package com.fintech.gateway.Config;

import feign.codec.Decoder;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringDecoder;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@Configuration
public class FeignConfiguration {

  @Bean
  public HttpMessageConverters messageConverters() {
    return new HttpMessageConverters(new MappingJackson2HttpMessageConverter());
  }

  @Bean
  public Decoder feignDecoder() {
    ObjectFactory<HttpMessageConverters> messageConverters = this::messageConverters;
    return new SpringDecoder(messageConverters);
  }

  @Bean
  public Encoder feignEncoder() {
    ObjectFactory<HttpMessageConverters> messageConverters = this::messageConverters;
    return new SpringFormEncoder(new SpringEncoder(messageConverters));
  }
}

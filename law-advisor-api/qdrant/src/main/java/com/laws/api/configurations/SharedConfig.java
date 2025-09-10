package com.laws.api.configurations;

import com.shared.mvc.interceptors.ResponseInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SharedConfig implements WebMvcConfigurer {
  @Bean
  public ResponseInterceptor responseInterceptor() {
    return new ResponseInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(responseInterceptor())
        .addPathPatterns("/api/**"); // Apply to all API endpoints
  }
}

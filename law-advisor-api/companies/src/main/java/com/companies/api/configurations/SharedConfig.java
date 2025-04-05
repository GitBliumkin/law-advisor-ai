package com.companies.api.configurations;

import com.shared.mvc.interceptors.ResponseInterceptor;
import com.shared.utils.JsonFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SharedConfig implements WebMvcConfigurer {
  @Value("${mock-data.path}") 
  private String mockDataPath;
	
  @Bean
  public ResponseInterceptor responseInterceptor() {
    return new ResponseInterceptor();
  }
  
  @Bean
  public JsonFactory jsonFactory() {
      return new JsonFactory(this.mockDataPath);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(responseInterceptor())
        .addPathPatterns("/api/**"); 
  }
}

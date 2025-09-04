package com.company.api.configurations;

import com.shared.mvc.interceptors.ResponseInterceptor;
import com.shared.utils.JsonFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
  @Bean
  public ResponseInterceptor responseInterceptor() {
    return new ResponseInterceptor();
  }
  
  @Bean
  public JsonFactory jsonFactory(@Value("${mock.data.path:mock-data}") String mockDataPath) {
      return new JsonFactory(mockDataPath);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(new ResponseInterceptor()).order(0);
  }
}

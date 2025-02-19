package com.shared.mvc.interceptors;

import com.shared.basecrud.dtos.responses.BaseResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.HandlerInterceptor;

public class ResponseInterceptor implements HandlerInterceptor {
  private static final Logger logger = LoggerFactory.getLogger(ResponseInterceptor.class);

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {
    logger.info("PreHandle: Processing request URI - {}", request.getRequestURI());
    return true; // Allow the request to proceed
  }

  @Override
  public void postHandle(
      HttpServletRequest request,
      HttpServletResponse response,
      Object handler,
      org.springframework.web.servlet.ModelAndView modelAndView) {
    logger.info("PostHandle: Processed request URI - {}", request.getRequestURI());
  }

  @Override
  public void afterCompletion(
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {}

  /** Wraps successful responses into ResponseEntity with appropriate HTTP status. */
  public ResponseEntity<Object> wrapResponse(HttpServletRequest request, Object response) {
    String uri = request.getRequestURI();
    HttpStatus status;

    if (response instanceof BaseResponse) {
      BaseResponse baseResponse = (BaseResponse) response;

      // Determine HTTP status dynamically
      if (baseResponse.getError() != null) {
        status = HttpStatus.INTERNAL_SERVER_ERROR;
      } else if (uri.contains("/create")) {
        status = HttpStatus.CREATED;
      } else if (uri.contains("/delete")) {
        status = HttpStatus.OK; // Return OK instead of NO_CONTENT to provide response data
      } else {
        status = HttpStatus.OK;
      }

      logger.info("Wrap Response: URI={}, Status={}, Response={}", uri, status, baseResponse);
      return ResponseEntity.status(status).body(baseResponse);
    }

    // If the response is not of type BaseResponse, default to OK.
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}

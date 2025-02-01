package com.shared.mvc.interceptors;

import com.shared.basecrud.dtos.responses.BaseResponse;
import com.shared.models.enums.Services;
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
      HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
    if (ex != null) {
      logger.error("Exception occurred: {}", ex.getMessage(), ex);
      wrapErrorResponse(ex, request.getRequestURI());
    }
  }

  /** Wraps successful responses into ResponseEntity with appropriate HTTP status. */
  public ResponseEntity<Object> wrapResponse(HttpServletRequest request, Object response) {
    String uri = request.getRequestURI();
    HttpStatus status;

    if (response instanceof BaseResponse) {
      BaseResponse baseResponse = (BaseResponse) response;

      // Determine HTTP status dynamically
      if (baseResponse.getError() != null) {
        return wrapErrorResponse(baseResponse, uri);
      } else if (uri.contains("/create")) {
        status = HttpStatus.CREATED;
      } else if (uri.contains("/delete")) {
        status = HttpStatus.OK; // Return OK instead of NO_CONTENT to provide response data
        baseResponse.setMessage("Entity successfully deleted.");
      } else {
        status = HttpStatus.OK;
      }

      logger.info("Wrap Response: URI={}, Status={}, Response={}", uri, status, baseResponse);
      return ResponseEntity.status(status).body(baseResponse);
    }

    // If the response is not of type BaseResponse, default to OK.
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /** Wraps error responses into ResponseEntity with structured error information. */
  protected ResponseEntity<Object> wrapErrorResponse(Object errorObject, String uri) {
    BaseResponse errorResponse;

    if (errorObject instanceof Exception) {
      Exception ex = (Exception) errorObject;
      logger.error("Error occurred: {}", ex.getMessage(), ex);
      errorResponse = new BaseResponse();
      errorResponse.setStatus("ERROR");
      errorResponse.setMessage(ex.getMessage());
      errorResponse.setError(ex.getClass().getSimpleName());
    } else {
      errorResponse = (BaseResponse) errorObject;
    }

    try {
      Services serviceName = getServiceName(uri);
      errorResponse.setServiceName(serviceName);
      errorResponse.setUri(uri);
    } catch (IllegalArgumentException e) {
      logger.error("Invalid service name in URI: {}", uri, e);
      errorResponse.setServiceName(null);
      errorResponse.setMessage("Invalid service name in URI");
    }

    logger.error("Error Response: {}", errorResponse);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  /** Extracts the service name from the URI. */
  public Services getServiceName(String uri) {
    String[] parts = uri.split("/");

    if (parts.length < 2) {
      throw new IllegalArgumentException("URI does not contain a valid service name: " + uri);
    }

    return Services.fromString(parts[1]);
  }
}

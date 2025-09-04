package com.shared.mvc.advice;

import com.shared.basecrud.dtos.responses.BaseResponse;
import com.shared.basecrud.dtos.responses.BaseResponse.Error;
import com.shared.basecrud.dtos.responses.BaseResponse.ErrorDetail;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestControllerAdvice
public class GlobalApiExceptionHandler {

  @Value("${spring.application.name:service}")
  private String serviceName;

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<BaseResponse<Void>> notFound(EntityNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(BaseResponse.error(serviceName, Error.of("NOT_FOUND", ex.getMessage())));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<BaseResponse<Void>> invalidBody(MethodArgumentNotValidException ex) {
    List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> new ErrorDetail(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
        .toList();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(serviceName, Error.of("VALIDATION_ERROR", "Validation failed", details)));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<BaseResponse<Void>> invalidParams(ConstraintViolationException ex) {
    List<ErrorDetail> details = ex.getConstraintViolations().stream()
        .map(cv -> new ErrorDetail(pathOf(cv), cv.getMessage(), cv.getInvalidValue()))
        .toList();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(serviceName, Error.of("CONSTRAINT_VIOLATION", "Invalid parameters", details)));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<BaseResponse<Void>> badRequest(IllegalArgumentException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(BaseResponse.error(serviceName, Error.of("BAD_REQUEST", ex.getMessage())));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<BaseResponse<Void>> internal(Exception ex) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(BaseResponse.error(serviceName, Error.of("INTERNAL_ERROR", "Internal error")));
  }

  private static String pathOf(ConstraintViolation<?> cv) {
    var path = cv.getPropertyPath();
    return path == null ? null : path.toString();
  }
}

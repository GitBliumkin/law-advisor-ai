package com.shared.basecrud.dtos.responses;

import com.shared.basecrud.dtos.BaseDto;
import com.shared.models.enums.Services;
import java.time.LocalDateTime;

public class BaseResponse extends BaseDto {

  private LocalDateTime responseTimestamp;
  private String status;
  private Services serviceName;
  private String message;
  private String error;
  private String uri;

  public BaseResponse() {
    this.responseTimestamp = LocalDateTime.now();
  }

  public void setErrorParameters(
      String error, String message, Services serviceName, String uri, String status) {
    this.status = status;
    this.error = error;
    this.message = message;
    this.serviceName = serviceName;
    this.uri = uri;
  }

  public BaseResponse(
      Services serviceName, String error, String message, String status, String uri) {
    this.status = status;
    this.error = error;
    this.message = message;
    this.serviceName = serviceName;
    this.uri = uri;
  }

  public LocalDateTime getResponseTimestamp() {
    return responseTimestamp;
  }

  public String getStatus() {
    return status;
  }

  public String setStatus(String status) {
    return status;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getError() {
    return error;
  }

  public void setError(String error) {
    this.error = error;
  }

  public Services getServiceName() {
    return serviceName;
  }

  public void setServiceName(Services serviceName) {
    this.serviceName = serviceName;
  }

  public String getUri() {
    return this.uri;
  }

  public void setUri(String uri) {
    this.uri = uri;
  }
}

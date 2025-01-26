package com.shared.basecrud.dtos.responses;

public class BaseErrorResponse extends BaseResponse {
  // TODO Implement ENUM
  private String error;
  private String message;
  // TODO Implement ENUM
  private String serviceName;

  public BaseErrorResponse(String error, String message, String serviceName) {
    super("ERROR");
    this.error = error;
    this.message = message;
    this.serviceName = serviceName;
  }

  public String getError() {
    return error;
  }

  public String getMessage() {
    return message;
  }

  public String getServiceName() {
    return serviceName;
  }
}

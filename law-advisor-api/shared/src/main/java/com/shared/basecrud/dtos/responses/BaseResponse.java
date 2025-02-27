package com.shared.basecrud.dtos.responses;

public class BaseResponse<T> {
  protected boolean success;
  protected String serviceName;
  protected T payload;
  protected Error error;

  protected BaseResponse(String serviceName, boolean success, T payload, String errorMessage) {
    this.success = success;
    this.serviceName = serviceName;
    this.payload = payload;
    this.error = errorMessage != null ? new Error(errorMessage) : null;
  }

  public static <T> BaseResponse<T> success(String serviceName, T payload) {
    return new BaseResponse<>(serviceName, true, payload, null);
  }
  
  public static <T> BaseResponse<T> success(String serviceName) {
	    return new BaseResponse<>(serviceName, true, null, null);
	  }

  public static <T> BaseResponse<T> error(String serviceName, String errorMessage) {
    return new BaseResponse<>(serviceName, false, null, errorMessage);
  }

  public static class Error {
    private final String errorMessage;

    public Error(String errorMessage) {
      this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
      return errorMessage;
    }
  }

  public boolean isSuccess() {
    return success;
  }

  public String getServiceName() {
    return serviceName;
  }

  public T getPayload() {
    return payload;
  }

  public Error getError() {
    return error;
  }
}

package com.shared.basecrud.dtos.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse<T> {
  private final boolean success;
  private final String serviceName;
  private final T payload;
  private final Error error;

  protected BaseResponse(String serviceName, boolean success, T payload, Error error) {
    this.success = success;
    this.serviceName = serviceName;
    this.payload = payload;
    this.error = error;
  }

  public static <T> BaseResponse<T> success(String serviceName, T payload) {
    return new BaseResponse<>(serviceName, true, payload, null);
  }

  public static <T> BaseResponse<T> success(String serviceName) {
    return new BaseResponse<>(serviceName, true, null, null);
  }

  public static <T> BaseResponse<T> error(String serviceName, Error error) {
    return new BaseResponse<>(serviceName, false, null, error);
  }

  // ---- Error types (don’t expose Spring classes) ----
  public static class Error {
    private final String code;                 // e.g. NOT_FOUND, VALIDATION_ERROR
    private final String message;              // human message
    private final List<ErrorDetail> details;   // optional per-field details

    public Error(String code, String message, List<ErrorDetail> details) {
      this.code = code;
      this.message = message;
      this.details = details;
    }

    public String getCode()     { return code; }
    public String getMessage()  { return message; }
    public List<ErrorDetail> getDetails() { return details; }

    public static Error of(String code, String message) {
      return new Error(code, message, null);
    }
    public static Error of(String code, String message, List<ErrorDetail> details) {
      return new Error(code, message, details);
    }
  }

  public static record ErrorDetail(String field, String message, Object rejectedValue) {}

  public boolean isSuccess()      { return success; }
  public String getServiceName()  { return serviceName; }
  public T getPayload()           { return payload; }
  public Error getError()         { return error; }
}

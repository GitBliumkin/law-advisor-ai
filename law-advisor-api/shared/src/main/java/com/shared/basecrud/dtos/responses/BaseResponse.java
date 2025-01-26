package com.shared.basecrud.dtos.responses;

import com.shared.basecrud.dtos.BaseDto;
import java.time.LocalDateTime;

public abstract class BaseResponse extends BaseDto {

  private LocalDateTime responseTimestamp;
  private String status;

  public BaseResponse(String status) {
    this.responseTimestamp = LocalDateTime.now();
    this.status = status;
  }

  public LocalDateTime getResponseTimestamp() {
    return responseTimestamp;
  }

  public String getStatus() {
    return status;
  }
}

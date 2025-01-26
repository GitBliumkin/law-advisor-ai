package com.shared.basecrud.dtos.requests;

import java.time.LocalDateTime;

public abstract class BaseRequest {

    private LocalDateTime requestTimestamp;

    public BaseRequest() {
        this.requestTimestamp = LocalDateTime.now();
    }

    public LocalDateTime getRequestTimestamp() {
        return requestTimestamp;
    }

    public void setRequestTimestamp(LocalDateTime requestTimestamp) {
        this.requestTimestamp = requestTimestamp;
    }
}

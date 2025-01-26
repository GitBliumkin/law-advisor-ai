package com.shared.basecrud.dtos.responses;

import java.time.LocalDateTime;
import java.util.List;

import com.shared.basecrud.dtos.tables.BaseTableRowDto;

public abstract class BaseResponse{

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

package com.shared.basecrud.dtos.responses;

import com.shared.basecrud.dtos.BaseDto;
import java.util.List;

public class BaseListResponse<T extends BaseDto> extends BaseResponse {
  private List<T> data;
  private Number count;
  private Number size;
  private Number totalCount;
  private Number totalPages;

  public BaseListResponse() {
    super("SUCCESS");
  }

  public List<T> getData() {
    return data;
  }

  public void setData(List<T> data) {
    this.data = data;
  }

  public Number getCount() {
    return count;
  }

  public void setCount(Number count) {
    this.count = count;
  }

  public Number getSize() {
    return size;
  }

  public void setSize(Number size) {
    this.size = size;
  }

  public Number getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(Number totalCount) {
    this.totalCount = totalCount;
  }

  public Number getTotalPages() {
    return totalPages;
  }

  public void setTotalPages(Number totalPages) {
    this.totalPages = totalPages;
  }
}

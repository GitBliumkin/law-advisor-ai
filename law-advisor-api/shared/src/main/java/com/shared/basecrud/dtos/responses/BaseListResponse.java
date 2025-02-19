package com.shared.basecrud.dtos.responses;

import com.shared.basecrud.dtos.BaseDto;
import java.util.List;

public class BaseListResponse<Dto extends BaseDto>
    extends BaseResponse<BaseListResponse.Payload<Dto>> {

  private BaseListResponse(
      String serviceName, boolean success, Payload<Dto> payload, String errorMessage) {
    super(serviceName, success, payload, errorMessage);
  }

  // ✅ Static factory method for success response
  public static <Dto extends BaseDto> BaseListResponse<Dto> success(
      String serviceName,
      List<Dto> data,
      Integer size,
      Integer page,
      Integer totalCount,
      Integer totalPages) {
    return new BaseListResponse<>(
        serviceName, true, new Payload<>(data, size, page, totalCount, totalPages), null);
  }

  // ✅ Static factory method for error response
  public static <Dto extends BaseDto> BaseListResponse<Dto> errorList(
      String serviceName, String errorMessage) {
    return new BaseListResponse<>(serviceName, false, null, errorMessage);
  }

  public static class Payload<Dto extends BaseDto> {
    private final List<Dto> data;
    private final Integer size;
    private final Integer page;
    private final Integer totalCount;
    private final Integer totalPages;

    public Payload(
        List<Dto> data, Integer size, Integer page, Integer totalCount, Integer totalPages) {
      this.data = data;
      this.size = size;
      this.page = page;
      this.totalCount = totalCount;
      this.totalPages = totalPages;
    }

    public List<Dto> getData() {
      return data;
    }

    public Integer getSize() {
      return size;
    }

    public Integer getPage() {
      return page;
    }

    public Integer getTotalCount() {
      return totalCount;
    }

    public Integer getTotalPages() {
      return totalPages;
    }
  }
}

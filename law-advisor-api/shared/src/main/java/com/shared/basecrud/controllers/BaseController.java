package com.shared.basecrud.controllers;

import com.shared.basecrud.dtos.BaseDto;
import com.shared.basecrud.dtos.requests.BaseRequest;
import com.shared.basecrud.dtos.responses.BaseListResponse;
import com.shared.basecrud.dtos.responses.BaseResponse;
import com.shared.basecrud.handlers.BaseHandler;
import com.shared.basecrud.tables.BaseTable;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

public abstract class BaseController<Request extends BaseRequest, Dto extends BaseDto, Table extends BaseTable> {

  @Value("${spring.application.name:service}")
  private String serviceName;

  protected final Logger logger;
  protected final BaseHandler<Request, Dto, Table> handler;

  protected BaseController(Class<?> controllerClass, BaseHandler<Request, Dto, Table> handler) {
    this.logger = LoggerFactory.getLogger(controllerClass);
    this.handler = handler;
  }

  @GetMapping
  @SuppressWarnings("unchecked")
  public BaseListResponse<Dto> getAll(
      @RequestParam(defaultValue = "-1") int page,
      @RequestParam(defaultValue = "-1") int size,
      @RequestParam(required = false) String query) {
    Map<String, Object> data = handler.getAll(size, page); // pass 'query' if your handler supports it
    return BaseListResponse.success(
        serviceName,
        (List<Dto>) data.get("data"),
        (Integer) data.get("size"),
        (Integer) data.get("page"),
        (Integer) data.get("totalCount"),
        (Integer) data.get("totalPages"));
  }

  @GetMapping("/{id}")
  public BaseResponse<Dto> getById(@PathVariable String id) {
    return BaseResponse.success(serviceName, handler.getById(id));
  }

  @PostMapping
  public BaseResponse<Dto> create(@Valid @RequestBody Request request) {
    return BaseResponse.success(serviceName, handler.save(request));
  }

  @PutMapping
  public BaseResponse<Dto> update(@Valid @RequestBody Request request) {
    return BaseResponse.success(serviceName, handler.save(request));
  }

  @DeleteMapping("/{id}")
  public BaseResponse<Void> delete(@PathVariable String id) {
    handler.softDelete(id);
    return BaseResponse.success(serviceName);
  }
}

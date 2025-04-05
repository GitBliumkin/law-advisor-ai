package com.shared.basecrud.controllers;

import com.shared.basecrud.dtos.BaseDto;
import com.shared.basecrud.dtos.requests.BaseRequest;
import com.shared.basecrud.dtos.responses.BaseListResponse;
import com.shared.basecrud.dtos.responses.BaseResponse;
import com.shared.basecrud.handlers.BaseHandler;
import com.shared.basecrud.tables.BaseTable;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public abstract class BaseController<
    Request extends BaseRequest, Dto extends BaseDto, Table extends BaseTable> {

  private String serviceName;
  private static Logger logger;
  protected final BaseHandler<Request, Dto, Table> handler;

  protected BaseController(Class serivce, BaseHandler<Request, Dto, Table> handler) {
    this.logger = LoggerFactory.getLogger(serivce);
    this.serviceName = serivce.getCanonicalName();
    this.handler = handler;
  }

  @GetMapping
  public BaseListResponse<Dto> getAll(
      @RequestParam(required = false, defaultValue = "-1") int page,
      @RequestParam(required = false, defaultValue = "-1") int size,
      @RequestParam(required = false) String query) {
    try {
      Map<String, Object> data = this.handler.getAll(size, page);
      return BaseListResponse.success(
          this.serviceName,
          (List<Dto>) data.get("data"),
          (Integer) data.get("size"),
          (Integer) data.get("page"),
          (Integer) data.get("totalCount"),
          (Integer) data.get("totalPages"));
    } catch (Exception e) {
      this.logger.error("Error caught in base get all", e);
      return BaseListResponse.errorList(this.serviceName, e.getMessage());
    }
  }

  @GetMapping("/{id}")
  public BaseResponse<Dto> getById(@PathVariable String id) {
    try {
      Dto dto = this.handler.getById(id);
      return BaseResponse.success(this.serviceName, dto);
    } catch (Exception e) {
      this.logger.error("Error caught in base get", e);
      return BaseResponse.error(this.serviceName, e.getMessage());
    }
  }

  @PostMapping
  public BaseResponse<Dto> create(@RequestBody Request request) {
    try {
      Dto dto = this.handler.save(request);
      return BaseResponse.success(this.serviceName, dto);
    } catch (Exception e) {
      this.logger.error("Error caught in base create", e);
      return BaseResponse.error(this.serviceName, e.getMessage());
    }
  }

  @PutMapping
  public BaseResponse<Dto> update(@RequestBody Request request) {
    try {
      Dto dto = this.handler.save(request);
      return BaseResponse.success(this.serviceName, dto);
    } catch (Exception e) {
      this.logger.error("Error caught in base update", e);
      return BaseResponse.error(this.serviceName, e.getMessage());
    }
  }

  @DeleteMapping("/{id}")
  public BaseResponse<Dto> delete(@PathVariable String id) {
    try {
      this.handler.softDelete(id);
      return BaseResponse.success(this.serviceName);
    } catch (Exception e) {
      this.logger.error("Error caught in base delete", e);

      return BaseResponse.error(this.serviceName, e.getMessage());
    }
  }
}

package com.shared.basecrud.controllers;

import com.shared.basecrud.dtos.requests.BaseRequest;
import com.shared.basecrud.dtos.responses.BaseListResponse;
import com.shared.basecrud.dtos.responses.BaseResponse;
import com.shared.basecrud.handlers.BaseHandlerService;
import com.shared.basecrud.tables.BaseTable;
import org.slf4j.Logger;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

public abstract class BaseController<
    Request extends BaseRequest,
    Response extends BaseResponse,
    ListResponse extends BaseListResponse<Response>,
    Table extends BaseTable> {

  private static Logger logger;
  protected final BaseHandlerService<Request, Response, ListResponse, Table> handler;

  protected BaseController(
      Logger logger, BaseHandlerService<Request, Response, ListResponse, Table> handler) {
    this.logger = logger;
    this.handler = handler;
  }

  @GetMapping
  public ListResponse getAll(
      @RequestParam(required = false, defaultValue = "-1") int page,
      @RequestParam(required = false, defaultValue = "-1") int size,
      @RequestParam(required = false) String query) {
    try {
      ListResponse listResponse = this.handler.getAll(size, page);
      return listResponse;
    } catch (Exception e) {
      return this.handler.createErrorListResponse(e);
    }
  }

  @GetMapping("/{id}")
  public Response getById(@PathVariable String id) {
    try {
      Response response = this.handler.getById(id);
      return response;
    } catch (Exception e) {
      return this.handler.createErrorResponse(e);
    }
  }

  @PostMapping
  public Response create(@RequestBody Request request) {
    try {
      Response response = this.handler.create(request);
      return response;
    } catch (Exception e) {
      return this.handler.createErrorResponse(e);
    }
  }

  @PutMapping("/{id}")
  public Response update(@PathVariable String id, @RequestBody Request request) {
    try {
      Response response = this.handler.update(request);
      return response;
    } catch (Exception e) {
      return this.handler.createErrorResponse(e);
    }
  }

  @DeleteMapping("/{id}")
  public Response delete(@PathVariable String id) {
    try {
      this.handler.delete(id);
      return this.handler.delete(id);
    } catch (Exception e) {
      return this.handler.createErrorResponse(e);
    }
  }
}

package com.shared.basecrud.controllers;

import com.shared.basecrud.dtos.requests.BaseRequest;
import com.shared.basecrud.dtos.responses.BaseErrorResponse;
import com.shared.basecrud.dtos.responses.BaseListResponse;
import com.shared.basecrud.dtos.responses.BaseResponse;
import com.shared.basecrud.handlers.BaseHandlerService;
import com.shared.basecrud.tables.BaseTable;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
      BaseHandlerService<Request, Response, ListResponse, Table> handler, Logger logger) {
    this.handler = handler;
    this.logger = logger;
  }

  protected ResponseEntity<Object> handleError(Exception e, String path) {
    logger.error("Error occurred: {}", e.getMessage(), e);
    BaseErrorResponse errorResponse =
        new BaseErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), e.getMessage(), path);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  @GetMapping
  public ResponseEntity<Object> getAll(
      @RequestParam(required = false, defaultValue = "-1") int page,
      @RequestParam(required = false, defaultValue = "-1") int size,
      @RequestParam(required = false) String query) {
    try {
      ListResponse listResponse = this.handler.getAll(size, page);
      return ResponseEntity.ok(listResponse);
    } catch (Exception e) {
      return handleError(e, "/");
    }
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getById(@PathVariable String id) {
    try {
      Response response = this.handler.getById(id);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return handleError(e, "/{id}");
    }
  }

  @PostMapping
  public ResponseEntity<Object> create(@RequestBody Request request) {
    try {
      Response response = this.handler.create(request);
      return ResponseEntity.status(HttpStatus.CREATED).body(response);
    } catch (Exception e) {
      return handleError(e, "/");
    }
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> update(@PathVariable String id, @RequestBody Request request) {
    try {
      Response response = this.handler.update(request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return handleError(e, "/{id}");
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> delete(@PathVariable String id) {
    try {
      this.handler.delete(id);
      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return handleError(e, "/{id}");
    }
  }
}

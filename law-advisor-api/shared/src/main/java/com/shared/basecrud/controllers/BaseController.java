package com.shared.basecrud.controllers;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.shared.basecrud.dtos.requests.BaseRequest;
import com.shared.basecrud.dtos.responses.BaseErrorResponse;
import com.shared.basecrud.dtos.responses.BaseResponse;
import com.shared.basecrud.dtos.tables.BaseTableRowDto;
import com.shared.basecrud.handlers.BaseHandlerService;

public abstract class BaseController<Request extends BaseRequest, Response extends BaseResponse, TableRow extends BaseTableRowDto, Handler extends BaseHandlerService<TableRow>> {

    private static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    protected abstract TableRow convertRequestToTableRow(Request request);

    protected abstract Response convertTableRowToResponse(TableRow row);

    protected abstract List<Response> convertTableRowToListResponse(List<TableRow> rows);

    protected final Handler handler;

    protected BaseController(Handler handler) {
        this.handler = handler;
    }

    protected ResponseEntity<Object> handleError(Exception e, String path) {
        logger.error("Error occurred: {}", e.getMessage(), e);
        BaseErrorResponse errorResponse = new BaseErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                e.getMessage(),
                path
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        try {
            List<TableRow> rows = this.handler.getAll();
            return ResponseEntity.ok(convertTableRowToListResponse(rows));
        } catch (Exception e) {
            return handleError(e, "/");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable String id) {
        try {
            TableRow row = this.handler.getById(id);
            return ResponseEntity.ok(this.convertTableRowToResponse(row));
        } catch (Exception e) {
            return handleError(e, "/{id}");
        }
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody Request request) {
        try {
            TableRow newTableRow = this.convertRequestToTableRow(request);
            TableRow createdRow = this.handler.create(newTableRow);
            return ResponseEntity.status(HttpStatus.CREATED).body(this.convertTableRowToResponse(createdRow));
        } catch (Exception e) {
            return handleError(e, "/");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable String id, @RequestBody Request request) {
        try {
            TableRow changedTableRow = this.convertRequestToTableRow(request);
            TableRow updatedTableRow = this.handler.update(changedTableRow);
            return ResponseEntity.ok(this.convertTableRowToResponse(updatedTableRow));
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

package com.shared.basecrud.handlers;

import com.shared.basecrud.dtos.requests.BaseRequest;
import com.shared.basecrud.dtos.responses.BaseListResponse;
import com.shared.basecrud.dtos.responses.BaseResponse;
import com.shared.basecrud.repositories.BaseRepository;
import com.shared.basecrud.tables.BaseTable;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public abstract class BaseHandlerService<
    Request extends BaseRequest,
    Response extends BaseResponse,
    ListResponse extends BaseListResponse<Response>,
    Table extends BaseTable> {

  private final BaseRepository<Table> repository;

  protected BaseHandlerService(BaseRepository<Table> repository) {
    this.repository = repository;
  }

  protected abstract Table converRequestToRow(Request request);

  protected abstract Response convertRowToResponse(Table object);

  protected abstract ListResponse convertRowsToListResponse(
      List<Table> rows, int size, int page, int totalCount, int totalPages);

  public abstract Response createErrorResponse(Exception error);

  public abstract ListResponse createErrorListResponse(Exception error);

  public ListResponse getAll(int size, int page) {
    if (size == -1 || page == -1) {
      List<Table> rows = repository.findAll();
      return convertRowsToListResponse(rows, rows.size(), 1, rows.size(), 1);
    }
    Page<Table> pageResult = repository.findAll(PageRequest.of(page, size));

    List<Table> rows = pageResult.getContent();
    long totalCount = pageResult.getTotalElements();
    int totalPages = pageResult.getTotalPages();

    return convertRowsToListResponse(rows, size, rows.size() / size, (int) totalCount, totalPages);
  }

  // TODO Change to custom exception
  public Response getById(String id) {
    Optional<Table> objectOptional = repository.findById(id);
    Table objcet =
        objectOptional.orElseThrow(
            () -> new IllegalArgumentException("Object not found for ID: " + id));
    return convertRowToResponse(objcet);
  }

  public Response create(Request request) {
    Table newRow = converRequestToRow(request);
    Table savedRow = repository.save(newRow);
    return convertRowToResponse(savedRow);
  }

  public Response update(Request request) {
    Table updatedRow = converRequestToRow(request);
    Table savedRow = repository.save(updatedRow);
    return convertRowToResponse(savedRow);
  }

  public Response delete(String id) {
    Optional<Table> entityOptional = repository.findById(id);
    Table entity =
        entityOptional.orElseThrow(
            () -> new IllegalArgumentException("Object not found for ID: " + id));
    repository.deleteById(id);
    Response response = convertRowToResponse(entity);
    return response;
  }
}

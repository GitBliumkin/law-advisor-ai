package com.shared.basecrud.handlers;

import com.shared.basecrud.dtos.BaseDto;
import com.shared.basecrud.dtos.requests.BaseRequest;
import com.shared.basecrud.repositories.BaseRepository;
import com.shared.basecrud.tables.BaseTable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public abstract class BaseHandler<
    Request extends BaseRequest, Dto extends BaseDto, Table extends BaseTable> {

  private final BaseRepository<Table, String> repository;

  protected BaseHandler(BaseRepository<Table, String> repository) {
    this.repository = repository;
  }

  public abstract Table converRequestToRow(Request request);

  public abstract Dto convertRowToDto(Table object);

  public Map<String, Object> convertRowsToPayloadMap(
    List<Table> rows, Integer size, Integer page, Integer totalCount, Integer totalPages) {
	Map<String, Object> payloadMap = new HashMap<String, Object>();
	
	List<Dto> dtos = convertRowsToDtoList(rows);

    payloadMap.put("data", dtos);
    payloadMap.put("size", size);
    payloadMap.put("page", page);
    payloadMap.put("totalCount", totalCount);
    payloadMap.put("totalPages", totalPages);
    return payloadMap;
  };
  
  public List<Dto> convertRowsToDtoList(List<Table> rows) {
	    List<Dto> dtos = new ArrayList<>();
	    rows.forEach(row -> dtos.add(convertRowToDto(row)));
	  return dtos;
  }
  
  public Dto save(Request request) {
	    Table newRow = converRequestToRow(request);
	    Table savedRow = repository.save(newRow);
	    return convertRowToDto(savedRow);
	  }
	  
	  public List<Dto> saveRows( List<Table> rowsToSave) {
		  Iterable<Table> iterable = rowsToSave;
		  List<Table> savedRows = repository.saveAll(iterable);
		  return convertRowsToDtoList(savedRows);
	  }
	  public Dto getById(String id) {
		    Optional<Table> objectOptional = repository.findByIdActive(id);
		    Table objcet =
		        objectOptional.orElseThrow(
		            () -> new IllegalArgumentException("Object not found for ID: " + id));
		    return convertRowToDto(objcet);
		  }  

  public Map<String, Object> getAll(int size, int page) {
    List<Table> rows;
    long totalCount;
    int totalPages;
    int currentPage;

    if (size == -1 || page == -1) {
      rows = repository.findAllActive();
      totalCount = rows.size();
      totalPages = 1;
      currentPage = 1;
    } else {
      Page<Table> pageResult = repository.findAllActive(PageRequest.of(page, size));

      rows = pageResult.getContent();
      totalCount = pageResult.getTotalElements();
      totalPages = pageResult.getTotalPages();
      currentPage = page;
    }

    return convertRowsToPayloadMap(rows, rows.size(), currentPage, (int) totalCount, totalPages);
  }

  public void softDelete(String id) {
    Optional<Table> entityOptional = repository.findById(id);
    Table object =
        entityOptional.orElseThrow(
            () -> new IllegalArgumentException("Object not found for ID: " + id));
    object.setDeletedBy(new UUID(0L, 0L).toString());
    object.setDeletedOn(LocalDateTime.now());
    repository.save(object);
  }
  
  public void delete(String id) {
	    Optional<Table> entityOptional = repository.findById(id);
	    Table object =
	        entityOptional.orElseThrow(
	            () -> new IllegalArgumentException("Object not found for ID: " + id));
	    object.setDeletedBy(new UUID(0L, 0L).toString());
	    object.setDeletedOn(LocalDateTime.now());
	    repository.save(object);
  }
  
  public void deleteMany(List<Table> entries) {;
	    repository.deleteAll(entries);
}
}

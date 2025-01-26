package com.companies.api.handlers;

import com.companies.api.repositories.CompanyProfileRepository;
import com.companies.api.tables.CompanyProfileTable;
import com.shared.basecrud.handlers.BaseHandlerService;
import com.shared.models.requests.CompanyProfileRequest;
import com.shared.models.responses.CompanyProfileListResponse;
import com.shared.models.responses.CompanyProfileResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyProfileHandlerService
    extends BaseHandlerService<
        CompanyProfileRequest,
        CompanyProfileResponse,
        CompanyProfileListResponse,
        CompanyProfileTable> {

  private final CompanyProfileRepository repository;

  @Autowired
  protected CompanyProfileHandlerService(CompanyProfileRepository repository) {
    super(repository);
    this.repository = repository;
  }

  @Override
  protected CompanyProfileTable converRequestToRow(CompanyProfileRequest request) {
    CompanyProfileTable row = new CompanyProfileTable();
    row.setCompanyName(request.getCompanyName());
    row.setProvince(request.getProvince());
    row.setCountry(request.getCountry());
    return table;
  }

  @Override
  protected CompanyProfileResponse convertRowToResponse(CompanyProfileTable row) {
    CompanyProfileResponse response = new CompanyProfileResponse();
    response.setId(row.getId());
    response.setCompanyName(row.getCompanyName());
    response.setProvince(row.getProvince());
    response.setCountry(row.getCountry());
    return response;
  }

  @Override
  protected CompanyProfileListResponse convertRowsToListResponse(
      List<CompanyProfileTable> rows, int size, long totalCount, int totalPages) {
    // Convert rows to DTOs
    List<CompanyProfileResponse> dtos =
        rows.stream().map(this::convertRowToResponse).collect(Collectors.toList());

    // Populate the response object
    CompanyProfileListResponse response = new CompanyProfileListResponse();
    response.setData(dtos);
    response.setCount(dtos.size());
    response.setSize(size);
    response.setTotalCount(totalCount);
    response.setTotalPages(totalPages);

    return response;
  }
}

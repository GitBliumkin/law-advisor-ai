package com.companies.api.handlers;

import com.companies.api.repositories.CompanyProfileRepository;
import com.companies.api.tables.CompanyProfileTable;
import com.shared.basecrud.handlers.BaseHandlerService;
import com.shared.models.requests.CompanyProfileRequest;
import com.shared.models.responses.CompanyProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyProfileHandlerService
    extends BaseHandlerService<CompanyProfileRequest, CompanyProfileDto, CompanyProfileTable> {

  @Autowired
  protected CompanyProfileHandlerService(CompanyProfileRepository repository) {
    super(repository);
  }

  @Override
  protected CompanyProfileTable converRequestToRow(CompanyProfileRequest request) {
    CompanyProfileTable row = new CompanyProfileTable();
    row.setId(request.getId());
    row.setName(request.getName());
    row.setProvince(request.getProvince());
    row.setCountry(request.getCountry());
    return row;
  }

  @Override
  protected CompanyProfileDto convertRowToDto(CompanyProfileTable row) {
    CompanyProfileDto dto = new CompanyProfileDto();
    dto.setId(row.getId());
    dto.setCompanyName(row.getName());
    dto.setProvince(row.getProvince());
    dto.setCountry(row.getCountry());
    return dto;
  }
}

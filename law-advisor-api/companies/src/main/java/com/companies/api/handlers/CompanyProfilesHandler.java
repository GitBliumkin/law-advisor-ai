package com.companies.api.handlers;

import com.companies.api.repositories.CompanyProfilesRepository;
import com.companies.api.tables.CompanyProfilesTable;
import com.shared.basecrud.handlers.BaseHandler;
import com.shared.models.requests.CompanyProfilesRequest;
import com.shared.models.responses.CompanyProfilesDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CompanyProfilesHandler
    extends BaseHandler<CompanyProfilesRequest, CompanyProfilesDto, CompanyProfilesTable> {

  @Autowired
  protected CompanyProfilesHandler(CompanyProfilesRepository repository) {
    super(repository);
  }

  @Override
  public CompanyProfilesTable converRequestToRow(CompanyProfilesRequest request) {
	  CompanyProfilesTable row = new CompanyProfilesTable();
    row.setId(request.getId());
    row.setName(request.getName());
    row.setProvince(request.getProvince());
    row.setCountry(request.getCountry());
    return row;
  }

  @Override
  public CompanyProfilesDto convertRowToDto(CompanyProfilesTable row) {
    CompanyProfilesDto dto = new CompanyProfilesDto();
    dto.setId(row.getId());
    dto.setCompanyName(row.getName());
    dto.setProvince(row.getProvince());
    dto.setCountry(row.getCountry());
    return dto;
  }
}

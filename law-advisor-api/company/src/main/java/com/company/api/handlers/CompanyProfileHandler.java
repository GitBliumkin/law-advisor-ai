package com.company.api.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.company.api.repositories.CompanyProfileRepository;
import com.company.api.tables.CompanyProfileTable;
import com.shared.basecrud.handlers.BaseHandler;
import com.shared.models.dtos.CompanyProfileDto;
import com.shared.models.requests.CompanyProfileRequest;

@Service
public class CompanyProfileHandler
    extends BaseHandler<CompanyProfileRequest, CompanyProfileDto, CompanyProfileTable> {

  @Autowired
  protected CompanyProfileHandler(CompanyProfileRepository repository) {
    super(repository);
  }

  @Override
  public CompanyProfileTable converRequestToRow(CompanyProfileRequest request) {
	  CompanyProfileTable row = new CompanyProfileTable();
    row.setId(request.getId());
    row.setName(request.getName());
    row.setProvince(request.getProvince());
    row.setCountry(request.getCountry());
    return row;
  }

  @Override
  public CompanyProfileDto convertRowToDto(CompanyProfileTable row) {
    CompanyProfileDto dto = new CompanyProfileDto();
    dto.setId(row.getId());
    dto.setName(row.getName());
    dto.setProvince(row.getProvince());
    dto.setCountry(row.getCountry());
    return dto;
  }
}

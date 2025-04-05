package com.laws.api.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.laws.api.repositories.LawUnitsAuditRepository;
import com.laws.api.tables.LawUnitsAuditTable;
import com.shared.basecrud.handlers.BaseHandlerService;
import com.shared.models.dtos.LawUnitsAuditDto;
import com.shared.models.requests.LawUnitsAuditRequest;

@Service
public class LawUnitsAuditHandler 
	extends BaseHandlerService<LawUnitsAuditRequest, LawUnitsAuditDto, LawUnitsAuditTable> {
	
	  @Autowired
	  protected LawUnitsAuditHandler(LawUnitsAuditRepository repository) {
	    super(repository);
	  }

	  @Override
	  protected LawUnitsAuditTable converRequestToRow(LawUnitsAuditRequest request) {
	    LawUnitsAuditTable row = new LawUnitsAuditTable();
	    row.setId(request.getId());
	    row.setLawId(request.getLawId());
	    row.setOrder(request.getOrder());
	    row.setParentId(request.getParentId());
	    row.setTitle(request.getTitle());
	    row.setUnitLabel(request.getUnitLabel());
	    row.setUnitType(request.getUnitType());
	    return row;
	  }

	  @Override
	  protected LawUnitsAuditDto convertRowToDto(LawUnitsAuditTable row) {
	    LawUnitsAuditDto dto = new LawUnitsAuditDto();
	    dto.setId(row.getId());
	    dto.setLawId(row.getLawId());
	    dto.setOrder(row.getOrder());
	    dto.setParentId(row.getParentId());
	    dto.setTitle(row.getTitle());
	    dto.setUnitLabel(row.getUnitLabel());
	    dto.setUnitType(row.getUnitType());
	    return dto;
	  }
	
}

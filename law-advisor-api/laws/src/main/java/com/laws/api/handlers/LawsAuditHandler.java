package com.laws.api.handlers;

import org.springframework.beans.factory.annotation.Autowired;

import com.laws.api.repositories.LawsAuditRepository;
import com.laws.api.tables.LawsAuditTable;
import com.shared.basecrud.handlers.BaseHandlerService;
import com.shared.models.dtos.LawsAuditDto;
import com.shared.models.requests.LawsAuditRequest;

public class LawsAuditHandler extends BaseHandlerService<LawsAuditRequest, LawsAuditDto, LawsAuditTable> {
	
	  @Autowired
	  protected LawsAuditHandler(LawsAuditRepository repository) {
	    super(repository);
	  }

	  @Override
	  protected LawsAuditTable converRequestToRow(LawsAuditRequest request) {
	    LawsAuditTable row = new LawsAuditTable();
	    row.setId(request.getId());
	    row.setIdentifire(request.getIdentifire());
	    row.setLawName(request.getLawName());
	    row.setRegion(request.getRegion());
	    row.setScrapeTime(request.getScrapeTime());
	    row.setUrl(request.getUrl());
	    return row;
	  }

	  @Override
	  protected LawsAuditDto convertRowToDto(LawsAuditTable row) {
	    LawsAuditDto dto = new LawsAuditDto();
	    dto.setId(row.getId());
	    dto.setIdentifire(row.getIdentifire());
	    dto.setLawName(row.getLawName());
	    dto.setRegion(row.getRegion());
	    dto.setScrapeTime(row.getScrapeTime());
	    dto.setUrl(row.getUrl());
	    return dto;
	  }
	
}

package com.law.api.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.law.api.repositories.LawAuditRepository;
import com.law.api.tables.LawAuditTable;
import com.shared.basecrud.handlers.BaseHandler;
import com.shared.models.dtos.LawAuditDto;
import com.shared.models.requests.LawAuditRequest;

@Service
public class LawAuditHandler extends BaseHandler<LawAuditRequest, LawAuditDto, LawAuditTable> {

	@Autowired
	protected LawAuditHandler(LawAuditRepository repository) {
		super(repository);
	}

	@Override
	public LawAuditTable converRequestToRow(LawAuditRequest request) {
		LawAuditTable row = new LawAuditTable();
		row.setId(request.getId());
		row.setIdentifire(request.getIdentifire());
		row.setName(request.getName());
		row.setRegion(request.getRegion());
		row.setScrapeTime(request.getScrapeTime());
		row.setUrl(request.getUrl());
		return row;
	}

	@Override
	public LawAuditDto convertRowToDto(LawAuditTable row) {
		LawAuditDto dto = new LawAuditDto();
		dto.setId(row.getId());
		dto.setIdentifire(row.getIdentifire());
		dto.setName(row.getName());
		dto.setRegion(row.getRegion());
		dto.setScrapeTime(row.getScrapeTime());
		dto.setUrl(row.getUrl());
		return dto;
	}

}

package com.qdrant.api.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qdrant.api.repositories.QdrantUnitsAuditRepository;
import com.qdrant.api.tables.QdrantUnitsAuditTable;
import com.shared.basecrud.handlers.BaseHandlerService;
import com.shared.models.dtos.QdrantUnitsAuditDto;
import com.shared.models.requests.QdrantUnitsAuditRequest;

@Service
public class QdrantUnitsAuditHandler 
	extends BaseHandlerService<QdrantUnitsAuditRequest, QdrantUnitsAuditDto, QdrantUnitsAuditTable> {
	
	  @Autowired
	  protected QdrantUnitsAuditHandler(QdrantUnitsAuditRepository repository) {
	    super(repository);
	  }

	  @Override
	  protected QdrantUnitsAuditTable converRequestToRow(QdrantUnitsAuditRequest request) {
	    QdrantUnitsAuditTable row = new QdrantUnitsAuditTable();
	    row.setId(request.getId());
	    row.setOrder(request.getOrder());
	    row.setTitle(request.getTitle());
	    row.setUnitLabel(request.getUnitLabel());
	    row.setUnitType(request.getUnitType());
	    return row;
	  }

	  @Override
	  protected QdrantUnitsAuditDto convertRowToDto(QdrantUnitsAuditTable row) {
	    QdrantUnitsAuditDto dto = new QdrantUnitsAuditDto();
	    dto.setId(row.getId());
	    dto.setOrder(row.getOrder());
	    dto.setTitle(row.getTitle());
	    dto.setUnitLabel(row.getUnitLabel());
	    dto.setUnitType(row.getUnitType());
	    return dto;
	  }
	
}

package com.qdrant.api.handlers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.qdrant.api.repositories.QdrantUnitsAuditRepository;
import com.qdrant.api.tables.QdrantUnitAuditTable;
import com.shared.basecrud.handlers.BaseHandler;
import com.shared.models.dtos.QdrantUnitAuditDto;
import com.shared.models.requests.QdrantUnitAuditRequest;

@Service
public class QdrantUnitAuditHandler extends BaseHandler<QdrantUnitAuditRequest, QdrantUnitAuditDto, QdrantUnitAuditTable> {
	
	  @Autowired
	  protected QdrantUnitAuditHandler(QdrantUnitsAuditRepository repository) {
	    super(repository);
	  }

	  @Override
	public QdrantUnitAuditTable converRequestToRow(QdrantUnitAuditRequest request) {
	    QdrantUnitAuditTable row = new QdrantUnitAuditTable();
	    row.setId(request.getId());
	    row.setOrder(request.getOrder());
	    row.setTitle(request.getTitle());
	    row.setUnitLabel(request.getUnitLabel());
	    row.setUnitType(request.getUnitType());
	    return row;
	  }

	  @Override
	public QdrantUnitAuditDto convertRowToDto(QdrantUnitAuditTable row) {
	    QdrantUnitAuditDto dto = new QdrantUnitAuditDto();
	    dto.setId(row.getId());
	    dto.setOrder(row.getOrder());
	    dto.setTitle(row.getTitle());
	    dto.setUnitLabel(row.getUnitLabel());
	    dto.setUnitType(row.getUnitType());
	    return dto;
	  }
	
}

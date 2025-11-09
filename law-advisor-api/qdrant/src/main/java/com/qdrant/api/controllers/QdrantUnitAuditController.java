package com.qdrant.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qdrant.api.handlers.QdrantUnitAuditHandler;
import com.qdrant.api.tables.QdrantUnitAuditTable;
import com.shared.basecrud.controllers.BaseController;
import com.shared.models.dtos.QdrantUnitAuditDto;
import com.shared.models.requests.QdrantUnitAuditRequest;

@RestController
@RequestMapping("/qdrant-unit-audit")
public class QdrantUnitAuditController extends BaseController<QdrantUnitAuditRequest, QdrantUnitAuditDto, QdrantUnitAuditTable>{

	@Autowired
	  protected QdrantUnitAuditController(QdrantUnitAuditHandler handler) {
	    super(QdrantUnitAuditController.class, handler);
	  }
}

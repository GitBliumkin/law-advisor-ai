package com.laws.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laws.api.handlers.LawUnitsAuditHandler;
import com.laws.api.tables.LawUnitsAuditTable;
import com.shared.basecrud.controllers.BaseController;
import com.shared.models.dtos.LawUnitsAuditDto;
import com.shared.models.requests.LawUnitsAuditRequest;

@RestController
@RequestMapping("/law-units-audit")
public class LawUnitsAuditController extends BaseController<LawUnitsAuditRequest, LawUnitsAuditDto, LawUnitsAuditTable> {

	  @Autowired
	  protected LawUnitsAuditController(LawUnitsAuditHandler handler) {
	    super(LawUnitsAuditController.class, handler);
	  }
	}
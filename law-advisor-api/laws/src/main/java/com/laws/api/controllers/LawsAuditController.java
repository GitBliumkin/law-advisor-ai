package com.laws.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.laws.api.handlers.LawsAuditHandler;
import com.laws.api.tables.LawsAuditTable;
import com.shared.basecrud.controllers.BaseController;
import com.shared.models.dtos.LawsAuditDto;
import com.shared.models.requests.LawsAuditRequest;

@RestController
@RequestMapping("/laws-audit")
public class LawsAuditController extends BaseController<LawsAuditRequest, LawsAuditDto, LawsAuditTable> {

	  @Autowired
	  protected LawsAuditController(LawsAuditHandler handler) {
	    super(LawsAuditController.class, handler);
	  }
	}
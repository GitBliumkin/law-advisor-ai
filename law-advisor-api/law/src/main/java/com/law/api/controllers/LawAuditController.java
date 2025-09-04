package com.law.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.law.api.handlers.LawAuditHandler;
import com.law.api.tables.LawAuditTable;
import com.shared.basecrud.controllers.BaseController;
import com.shared.models.dtos.LawAuditDto;
import com.shared.models.requests.LawAuditRequest;

@RestController
@RequestMapping("/law-audit")
public class LawAuditController extends BaseController<LawAuditRequest, LawAuditDto, LawAuditTable> {

	@Autowired
	protected LawAuditController(LawAuditHandler handler) {
		super(LawAuditController.class, handler);
	}
}
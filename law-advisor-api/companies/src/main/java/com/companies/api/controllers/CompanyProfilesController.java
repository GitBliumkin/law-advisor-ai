package com.companies.api.controllers;

import com.companies.api.handlers.CompanyProfilesHandler;
import com.companies.api.tables.CompanyProfilesTable;
import com.shared.basecrud.controllers.BaseController;
import com.shared.models.requests.CompanyProfilesRequest;
import com.shared.models.responses.CompanyProfilesDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company-profiles")
public class CompanyProfilesController
    extends BaseController<CompanyProfilesRequest, CompanyProfilesDto, CompanyProfilesTable> {

  @Autowired
  protected CompanyProfilesController(CompanyProfilesHandler handler) {
    super(CompanyProfilesController.class, handler);
  }
}

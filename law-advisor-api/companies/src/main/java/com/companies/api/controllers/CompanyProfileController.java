package com.companies.api.controllers;

import com.companies.api.handlers.CompanyProfileHandlerService;
import com.companies.api.tables.CompanyProfileTable;
import com.shared.basecrud.controllers.BaseController;
import com.shared.models.requests.CompanyProfileRequest;
import com.shared.models.responses.CompanyProfileDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company-profile")
public class CompanyProfileController
    extends BaseController<CompanyProfileRequest, CompanyProfileDto, CompanyProfileTable> {

  @Autowired
  protected CompanyProfileController(CompanyProfileHandlerService handler) {
    super(CompanyProfileController.class, handler);
  }
}

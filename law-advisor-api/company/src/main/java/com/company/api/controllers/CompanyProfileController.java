package com.company.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.api.handlers.CompanyProfileHandler;
import com.company.api.tables.CompanyProfileTable;
import com.shared.basecrud.controllers.BaseController;
import com.shared.models.dtos.CompanyProfileDto;
import com.shared.models.requests.CompanyProfileRequest;

@RestController
@RequestMapping("/company-profiles")
public class CompanyProfileController
    extends BaseController<CompanyProfileRequest, CompanyProfileDto, CompanyProfileTable> {

  @Autowired
  protected CompanyProfileController(CompanyProfileHandler handler) {
    super(CompanyProfileController.class, handler);
  }
}

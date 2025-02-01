package com.companies.api.controllers;

import com.companies.api.handlers.CompanyProfileHandlerService;
import com.companies.api.tables.CompanyProfileTable;
import com.shared.basecrud.controllers.BaseController;
import com.shared.models.requests.CompanyProfileRequest;
import com.shared.models.responses.CompanyProfileListResponse;
import com.shared.models.responses.CompanyProfileResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/company-profile")
public class CompanyProfileController
    extends BaseController<
        CompanyProfileRequest,
        CompanyProfileResponse,
        CompanyProfileListResponse,
        CompanyProfileTable> {

  private CompanyProfileHandlerService handler;
  private static final Logger logger = LoggerFactory.getLogger(CompanyProfileController.class);

  @Autowired
  protected CompanyProfileController(CompanyProfileHandlerService handler) {
    super(logger, handler);
  }
}

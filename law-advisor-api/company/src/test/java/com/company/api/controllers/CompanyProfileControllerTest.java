package com.company.api.controllers;

import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import com.company.api.configurations.WebConfig;
import com.company.api.handlers.CompanyProfileHandler;
import com.company.api.tables.CompanyProfileTable;
import com.shared.basecrud.controllers.BaseControllerTest;
import com.shared.models.dtos.CompanyProfileDto;
import com.shared.models.requests.CompanyProfileRequest;

@WebMvcTest(controllers = CompanyProfileController.class)
@Import(WebConfig.class)
@TestPropertySource(properties = "mock.data.path=mock-data")
@TestPropertySource(properties = "spring.application.name=company")
@TestInstance(Lifecycle.PER_CLASS)
class CompanyProfileControllerTest extends BaseControllerTest<
        CompanyProfileRequest, CompanyProfileDto, CompanyProfileTable, CompanyProfileController> {

    CompanyProfileControllerTest() { this.tableName = "company-profile";  this.serviceName = "company";}
    
    @MockBean
    private CompanyProfileHandler companyProfileHandler;

    @Override
    protected String getBaseUrl() { return "/company-profile"; }
}

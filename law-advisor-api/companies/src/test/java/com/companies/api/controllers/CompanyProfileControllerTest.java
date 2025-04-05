package com.companies.api.controllers;

import com.companies.api.handlers.CompanyProfilesHandler;
import com.companies.api.tables.CompanyProfilesTable;
import com.shared.basecrud.controllers.BaseControllerTest;
import com.shared.models.requests.CompanyProfilesRequest;
import com.shared.models.responses.CompanyProfilesDto;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CompanyProfilesController.class)
public class CompanyProfileControllerTest extends BaseControllerTest<
        CompanyProfilesRequest, 
        CompanyProfilesDto, 
        CompanyProfilesTable, 
        CompanyProfilesController> {
	
	public CompanyProfileControllerTest() {
		this.tableName = "company_profiles";
	}

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyProfilesController companyProfileController;

    @MockBean
    private CompanyProfilesHandler companyProfileHandler;

    protected String getBaseUrl() {
        return "/company-profiles";
    }
}

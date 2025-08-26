package com.companies.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.company.api.controllers.CompanyProfileController;
import com.company.api.handlers.CompanyProfileHandler;
import com.company.api.tables.CompanyProfileTable;
import com.shared.basecrud.controllers.BaseControllerTest;
import com.shared.models.dtos.CompanyProfileDto;
import com.shared.models.requests.CompanyProfileRequest;

@WebMvcTest(controllers = CompanyProfileController.class)
public class CompanyProfileControllerTest extends BaseControllerTest<CompanyProfileRequest, 
																     CompanyProfileDto, 
																     CompanyProfileTable, 
																     CompanyProfileController> {
	
	public CompanyProfileControllerTest() {
		this.tableName = "company_profile";
	}

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CompanyProfileHandler companyProfileHandler;

    protected String getBaseUrl() {
        return "/company-profile";
    }
}

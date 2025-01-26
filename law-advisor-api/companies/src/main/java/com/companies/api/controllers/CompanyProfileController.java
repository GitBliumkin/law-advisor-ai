package com.companies.api.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.companies.api.dtos.CompanyProfileDto;
import com.companies.api.handlers.CompanyProfileHandlerService;
import com.shared.basecrud.controllers.BaseController;
import com.shared.basecrud.handlers.BaseHandlerService;
import com.shared.models.requests.CompanyProfileRequest;
import com.shared.models.responses.CompanyProfileResponse;

@RestController
@RequestMapping( "/company-profile" )
public class CompanyProfileController extends BaseController<CompanyProfileRequest, CompanyProfileResponse, CompanyProfileDto, CompanyProfileHandlerService>{

	protected CompanyProfileController(CompanyProfileHandlerService handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected CompanyProfileDto convertRequestToTableRow(CompanyProfileRequest request) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected CompanyProfileResponse convertTableRowToResponse(CompanyProfileDto row) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<CompanyProfileResponse> convertTableRowToListResponse(List<CompanyProfileDto> rows) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

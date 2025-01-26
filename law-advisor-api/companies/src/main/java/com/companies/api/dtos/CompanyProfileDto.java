package com.companies.api.dtos;

import com.shared.basecrud.dtos.tables.BaseTableRowDto;

public class CompanyProfileDto extends BaseTableRowDto{
	    private String companyName;

	    public String getCompanyName() {
	        return companyName;
	    }

	    public void setCompanyName(String companyName) {
	        this.companyName = companyName;
	    }
}

package com.shared.models.dtos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.shared.models.enums.SupportedRegions;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScraperDto {
	private SupportedRegions region;
    private String status;
    private String message;
    private List<ScrapedPageDto> pages;
    
	public SupportedRegions getRegion() {
		return region;
	}
	public void setRegion(SupportedRegions region) {
		this.region = region;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<ScrapedPageDto> getPages() {
		return pages;
	}
	public void setPages(List<ScrapedPageDto> pages) {
		this.pages = pages;
	}
    
    
}

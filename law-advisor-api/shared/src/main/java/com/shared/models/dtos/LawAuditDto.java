package com.shared.models.dtos;

import com.shared.basecrud.dtos.BaseDto;
import com.shared.models.enums.ScraperIdentifires;
import com.shared.models.enums.SupportedRegions;

public class LawAuditDto extends BaseDto {
	
	private String id;
	
	private String name;
	
	private SupportedRegions region;
	
	private String url;
	
	private ScraperIdentifires identifire;
	
	private String scrapeTime;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SupportedRegions getRegion() {
		return region;
	}

	public void setRegion(SupportedRegions region) {
		this.region = region;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public ScraperIdentifires getIdentifire() {
		return identifire;
	}

	public void setIdentifire(ScraperIdentifires identifire) {
		this.identifire = identifire;
	}

	public String getScrapeTime() {
		return scrapeTime;
	}

	public void setScrapeTime(String scrapeTime) {
		this.scrapeTime = scrapeTime;
	}
}

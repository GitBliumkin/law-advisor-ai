package com.law.api.tables;

import com.shared.basecrud.tables.BaseTable;
import com.shared.models.enums.ScraperIdentifires;
import com.shared.models.enums.SupportedRegions;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "law_audit")
public class LawAuditTable extends BaseTable {

	@Column(nullable = false)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private SupportedRegions region;

	@Column(nullable = false)
	private String url;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ScraperIdentifires identifire;

	@Column(nullable = false)
	private String scrapeTime;

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

package com.shared.models.dtos;

public class ScrapedPageDto {
	private String identifire;
    private String lawName;
    private String url;
    private String scrapeTime; // You can parse this later
    private String pageContent;
    
	public String getIdentifire() {
		return identifire;
	}
	public void setIdentifire(String identifire) {
		this.identifire = identifire;
	}
	public String getLawName() {
		return lawName;
	}
	public void setLawName(String lawName) {
		this.lawName = lawName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getScrapeTime() {
		return scrapeTime;
	}
	public void setScrapeTime(String scrapeTime) {
		this.scrapeTime = scrapeTime;
	}
	public String getPageContent() {
		return pageContent;
	}
	public void setPageContent(String pageContent) {
		this.pageContent = pageContent;
	}
}

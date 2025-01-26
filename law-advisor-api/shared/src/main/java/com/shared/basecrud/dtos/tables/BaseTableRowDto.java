package com.shared.basecrud.dtos.tables;

import java.time.LocalDateTime;

public class BaseTableRowDto {
	 	private String id;
	    private LocalDateTime createdOn;
	    private String createdBy;
	    private LocalDateTime updatedOn;
	    private String updatedBy;
	    private LocalDateTime deletedOn;
	    private String deletedBy;

	    public String getId() {
	        return id;
	    }

	    public void setId(String id) {
	        this.id = id;
	    }

	    public LocalDateTime getCreatedOn() {
	        return createdOn;
	    }

	    public void setCreatedOn(LocalDateTime createdOn) {
	        this.createdOn = createdOn;
	    }

	    public String getCreatedBy() {
	        return createdBy;
	    }

	    public void setCreatedBy(String createdBy) {
	        this.createdBy = createdBy;
	    }

	    public LocalDateTime getUpdatedOn() {
	        return updatedOn;
	    }

	    public void setUpdatedOn(LocalDateTime updatedOn) {
	        this.updatedOn = updatedOn;
	    }

	    public String getUpdatedBy() {
	        return updatedBy;
	    }

	    public void setUpdatedBy(String updatedBy) {
	        this.updatedBy = updatedBy;
	    }

	    public LocalDateTime getDeletedOn() {
	        return deletedOn;
	    }

	    public void setDeletedOn(LocalDateTime deletedOn) {
	        this.deletedOn = deletedOn;
	    }

	    public String getDeletedBy() {
	        return deletedBy;
	    }

	    public void setDeletedBy(String deletedBy) {
	        this.deletedBy = deletedBy;
	    }
}

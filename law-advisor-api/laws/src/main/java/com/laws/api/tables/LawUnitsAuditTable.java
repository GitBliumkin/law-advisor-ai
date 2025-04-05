package com.laws.api.tables;

import com.shared.basecrud.tables.BaseTable;
import com.shared.models.enums.UnitType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "law_units_audit")
public class LawUnitsAuditTable extends BaseTable {
	
	@ManyToOne(optional = false)
    @JoinColumn(name = "lawId", referencedColumnName = "id", nullable = false)
    private LawsAuditTable law;

    @ManyToOne
    @JoinColumn(name = "parentId", referencedColumnName = "id")
    private LawUnitsAuditTable parent;

	@Column(nullable = false)
	private String lawId;
	
	@Column(nullable = false)
	private String parentId ;
	
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private UnitType unitType;
	
	@Column(nullable = false)
	private String unitLabel;
	
	@Column(nullable = false)
	private String title;
	
	@Column(nullable = false)
	private String content;
	
	@Column(name = "`order`", nullable = false)
	private Integer order;

	public String getLawId() {
		return lawId;
	}

	public void setLawId(String lawId) {
		this.lawId = lawId;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public UnitType getUnitType() {
		return unitType;
	}

	public void setUnitType(UnitType unitType) {
		this.unitType = unitType;
	}

	public String getUnitLabel() {
		return unitLabel;
	}

	public void setUnitLabel(String unitLabel) {
		this.unitLabel = unitLabel;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}
}

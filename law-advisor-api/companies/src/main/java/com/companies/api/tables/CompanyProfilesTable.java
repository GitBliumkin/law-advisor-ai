package com.companies.api.tables;

import com.shared.basecrud.tables.BaseTable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "company_profiles")
public class CompanyProfilesTable extends BaseTable {

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String province;

  @Column(nullable = false)
  private String country;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getProvince() {
    return province;
  }

  public void setProvince(String province) {
    this.province = province;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }
}

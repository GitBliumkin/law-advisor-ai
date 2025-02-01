package com.shared.models.responses;

import com.shared.basecrud.dtos.responses.BaseResponse;
import java.util.UUID;

public class CompanyProfileResponse extends BaseResponse {
  private UUID id;
  private String companyName;
  private String province;
  private String country;

  public CompanyProfileResponse() {}

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getCompanyName() {
    return companyName;
  }

  public void setCompanyName(String companyName) {
    this.companyName = companyName;
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

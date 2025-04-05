package com.shared.models.enums;

import com.shared.basecrud.dtos.enums.BaseEnum;

public enum SupportedRegions implements BaseEnum<SupportedRegions> {
	  Federal,
	  Ontario,
	  BritishColumbia;

	  public static SupportedRegions fromString(String value) {
	    return BaseEnum.fromString(SupportedRegions.class, value);
	  }
}

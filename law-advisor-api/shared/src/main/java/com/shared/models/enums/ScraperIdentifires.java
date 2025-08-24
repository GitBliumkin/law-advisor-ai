package com.shared.models.enums;

import com.shared.basecrud.dtos.enums.BaseEnum;

public enum ScraperIdentifires implements BaseEnum<ScraperIdentifires> {
	  FED,
	  FED_CLC,
	  FED_CHRA,
	  ON,
	  ON_ESA,
	  ON_OHSA,
	  BC,
	  BC_ESA,
	  BC_WCA;

	  public static ScraperIdentifires fromString(String value) {
	    return BaseEnum.fromString(ScraperIdentifires.class, value);
	  }
}

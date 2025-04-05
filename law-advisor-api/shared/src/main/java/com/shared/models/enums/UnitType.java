package com.shared.models.enums;

import com.shared.basecrud.dtos.enums.BaseEnum;

public enum UnitType implements BaseEnum<UnitType> {
	TITLE,
    PREAMBLE,
    PART,
    DIVISION,
    SUBDIVISION,
    SECTION,
    SUBSECTION,
    PARAGRAPH,
    SUBPARAGRAPH,
    CLAUSE,
    SCHEDULE,
    ANNEX,
    TABLE,
    FORM,
    HEADING,
    NOTE;

	public static UnitType fromString(String value) {
	  return BaseEnum.fromString(UnitType.class, value);
	}
}
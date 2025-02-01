package com.shared.models.enums;

import com.shared.basecrud.dtos.enums.BaseEnum;

public enum Services implements BaseEnum<Services> {
  Companies,
  Laws;

  public static Services fromString(String value) {
    return BaseEnum.fromString(Services.class, value);
  }
}

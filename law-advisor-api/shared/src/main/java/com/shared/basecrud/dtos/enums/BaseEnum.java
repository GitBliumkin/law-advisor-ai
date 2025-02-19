package com.shared.basecrud.dtos.enums;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface BaseEnum<T extends Enum<T>> {

  static <E extends Enum<E> & BaseEnum<E>> E fromString(Class<E> enumClass, String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("Value cannot be null or empty");
    }

    Map<String, E> lookup =
        Stream.of(enumClass.getEnumConstants())
            .collect(Collectors.toMap(e -> e.name().toLowerCase(), e -> e));

    E result = lookup.get(value.trim().toLowerCase());
    if (result == null) {
      throw new IllegalArgumentException("No enum constant for value: " + value);
    }
    return result;
  }
}

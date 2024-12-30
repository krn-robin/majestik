package com.keronic.majestik.language.utils;

import com.keronic.majestik.MajestikRuntimeException;

public class MagikObjectUtils {

  @SuppressWarnings("java:S100")
  public static boolean should_be_boolean(Object value) {
    try {
      var bool = (Boolean) value;

      return bool.booleanValue();
    } catch (Exception e) {

      throw new MajestikRuntimeException(e);
    }
  }

  public static boolean is(Object obj1, Object obj2) {
    if (obj2 == null) return obj1 == obj2;
    return switch (obj1) {
      case null -> obj1 == obj2;
      case Number num -> num.equals(obj2);
      default -> obj1 == obj2;
    };
  }

  /** Private constructor to prevent instantiation. */
  private MagikObjectUtils() {}
}

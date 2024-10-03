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

  /** Private constructor to prevent instantiation. */
  private MagikObjectUtils() {}
}

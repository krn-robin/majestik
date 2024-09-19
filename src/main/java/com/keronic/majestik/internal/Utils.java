package com.keronic.majestik.internal;

import module java.base;

import com.keronic.majestik.MajestikRuntimeException;

public class Utils {
  private Utils() {}

  public static MethodHandle findStatic(Class<?> refc, String name, MethodType type) {
    try {
			return MethodHandles.lookup().findStatic(refc, name, type);
    } catch (Exception e) {
      throw new MajestikRuntimeException(e);
    }
  }
}

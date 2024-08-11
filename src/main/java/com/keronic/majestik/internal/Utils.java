package com.keronic.majestik.internal;

import com.keronic.majestik.MajestikRuntimeException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

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

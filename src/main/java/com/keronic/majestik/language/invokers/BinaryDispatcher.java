package com.keronic.majestik.language.invokers;

import com.keronic.MajestikRuntimeException;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class BinaryDispatcher {
  private static MethodHandle todo;

  static {
    try {
      todo =
          MethodHandles.lookup()
              .findStatic(BinaryDispatcher.class, "todo", MethodType.genericMethodType(2));
    } catch (Exception e) {
      throw new MajestikRuntimeException(e);
    }
  }

  private BinaryDispatcher() {}

  public static Object todo(Object o1, Object o2) {
    if ((o1 instanceof Long l1) && (o2 instanceof Long l2)) return Long.sum(l1, l2);
    throw new UnsupportedOperationException(
        String.format("Not implemented: %s %s", o1.getClass(), o2.getClass()));
  }

  @SuppressWarnings("java:S1172")
  public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
    if (name.equals("+")) return new ConstantCallSite(todo);
    throw new UnsupportedOperationException(String.format("Not implemented: operator %s ", name));
  }
}

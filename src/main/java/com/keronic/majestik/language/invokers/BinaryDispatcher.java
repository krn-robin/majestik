package com.keronic.majestik.language.invokers;

import com.keronic.majestik.internal.Utils;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * BinaryDispatcher is responsible for handling binary operations and dispatching them to
 * appropriate handlers.
 */
public class BinaryDispatcher {
  private static MethodHandle todo =
      Utils.findStatic(BinaryDispatcher.class, "todo", MethodType.genericMethodType(2));

  /**
   * Performs a binary operation on two objects.
   *
   * @param o1 the first operand
   * @param o2 the second operand
   * @return the result of the operation
   */
  public static Object todo(Object o1, Object o2) {
    if ((o1 instanceof Long l1) && (o2 instanceof Long l2)) return Long.sum(l1, l2);
    throw new UnsupportedOperationException(
        String.format("Not implemented: %s %s", o1.getClass(), o2.getClass()));
  }

  /**
   * @param lookup
   * @param name
   * @param type
   * @return
   */
  @SuppressWarnings("java:S1172")
  public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
    if (name.equals("+")) return new ConstantCallSite(todo);
    throw new UnsupportedOperationException(String.format("Not implemented: operator %s ", name));
  }

  /** Private constructor to prevent instantiation. */
  private BinaryDispatcher() {}
}

package com.keronic.majestik.language.invokers;

import module java.base;

import com.keronic.majestik.internal.Utils;

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
        String.format(
            "Operator not implemented for types: %s and %s", o1.getClass(), o2.getClass()));
  }

  /**
   * Creates a call site for binary operations.
   *
   * @param lookup the lookup context
   * @param name the name of the operation
   * @param type the method type
   * @return a constant call site for the operation
   */
  @SuppressWarnings("java:S1172")
  public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
    if (name.equals("+")) return new ConstantCallSite(todo);
    throw new UnsupportedOperationException(String.format("Not implemented: operator %s ", name));
  }

  /** Private constructor to prevent instantiation. */
  private BinaryDispatcher() {}
}

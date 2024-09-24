package com.keronic.majestik.language.invokers;

import module java.base;

import com.keronic.majestik.internal.Utils;
import com.keronic.majestik.language.ResultTuple;

public class TupleBuilder {
  private static MethodHandle tuplecreator =
      Utils.findStatic(
          ResultTuple.class,
          "create",
          MethodType.methodType(ResultTuple.class, Object.class.arrayType()));

  @SuppressWarnings("java:S1172")
  public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
    return new ConstantCallSite(
        tuplecreator.asCollector(Object.class.arrayType(), type.parameterCount()));
    }

  /** Private constructor to prevent instantiation. */
  private TupleBuilder() {}
}

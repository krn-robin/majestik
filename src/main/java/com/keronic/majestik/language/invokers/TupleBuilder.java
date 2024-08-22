package com.keronic.majestik.language.invokers;

import com.keronic.majestik.internal.Utils;
import com.keronic.majestik.language.ResultTuple;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class TupleBuilder {
  private static MethodHandle tuplecreator =
      Utils.findStatic(
          ResultTuple.class,
          "create",
          MethodType.methodType(ResultTuple.class, Object.class.arrayType()));

  public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
    return new ConstantCallSite(
        tuplecreator.asCollector(Object.class.arrayType(), type.parameterCount()));
    }

  /** Private constructor to prevent instantiation. */
  private TupleBuilder() {}
}

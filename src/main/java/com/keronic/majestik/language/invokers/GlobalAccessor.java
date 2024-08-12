/** */
package com.keronic.majestik.language.invokers;

import com.keronic.majestik.internal.Utils;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/** */
public class GlobalAccessor {
  private static final Class<?> package_class = com.keronic.majestik.runtime.objects.Package.class;
  private static final MethodHandle getGlobal =
      Utils.findStatic(
          package_class, "get", MethodType.methodType(Object.class, String.class, String.class));

  /**
   * @param lookup
   * @param name
   * @param type
   * @param packageName
   * @param global
   * @return
   */
  @SuppressWarnings("java:S1172")
  public static CallSite bootstrapFetcher2(
      MethodHandles.Lookup lookup,
      String name,
      MethodType type,
      String packageName,
      String global) {
        return new ConstantCallSite(MethodHandles.insertArguments(getGlobal, 0, packageName, global));
    }

  /** */
  private GlobalAccessor() {}
}

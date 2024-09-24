/** */
package com.keronic.majestik.language.invokers;

import module java.base;

import com.keronic.majestik.internal.Utils;

/** */
public class GlobalAccessor {
  private static final Class<?> package_class = com.keronic.majestik.runtime.objects.Package.class;
  private static final MethodHandle getGlobal =
      Utils.findStatic(
          package_class, "get", MethodType.methodType(Object.class, String.class, String.class));

  private static final MethodHandle putGlobal =
      Utils.findStatic(
          package_class,
          "put",
          MethodType.methodType(void.class, String.class, String.class, Object.class));

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

  /**
   * Creates a CallSite for storing global variables.
   *
   * @param lookup the lookup context
   * @param name the name of the method
   * @param type the method type
   * @param packageName the name of the package
   * @param global the global variable name
   * @return a CallSite for storing global variables
   */
  @SuppressWarnings("java:S1172")
  public static CallSite bootstrapStorer2(
      MethodHandles.Lookup lookup,
      String name,
      MethodType type,
      String packageName,
      String global) {
    return new ConstantCallSite(MethodHandles.insertArguments(putGlobal, 0, packageName, global));
  }

  /** Private constructor to prevent instantiation. */
  private GlobalAccessor() {}
}

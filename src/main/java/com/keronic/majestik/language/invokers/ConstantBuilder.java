package com.keronic.majestik.language.invokers;

import module java.base;

import com.keronic.majestik.runtime.Proc;

public class ConstantBuilder {
  /**
   * Creates a call site for string operations.
   *
   * @param lookup the lookup context
   * @param name the name of the operation
   * @param type the method type
   * @param aString the string to format
   * @return a constant call site for the operation
   */
  @SuppressWarnings("java:S1172")
  public static CallSite stringBootstrap(
      MethodHandles.Lookup lookup, String name, MethodType type, String aString) {
    var bla = MethodHandles.constant(Object.class, String.format(aString));
    return new ConstantCallSite(bla);
  }

  /**
   * Creates a call site for a procedure with an empty environment.
   *
   * @param lookup the lookup context
   * @param name the name of the operation
   * @param type the method type
   * @param aClass the class containing the method
   * @param jMethodName the Java method name
   * @param magikMethodName the Magik method name
   * @param numArgs the number of arguments
   * @param mandatoryArgs the number of mandatory arguments
   * @param iterator flag indicating if an iterator is used
   * @param primNo the primitive number
   * @return a constant call site for the procedure
   * @throws Throwable if an error occurs during call site creation
   */
  @SuppressWarnings({"java:S107", "java:S1172"})
  public static CallSite procWithEmptyEnvBootstrap(
      MethodHandles.Lookup lookup,
      String name,
      MethodType type,
      Class<?> aClass,
      String jMethodName,
      String magikMethodName,
      int numArgs,
      int mandatoryArgs,
      int iterator,
      int primNo)
      throws Throwable {
    Proc aProc =
        Proc.of(aClass, jMethodName, magikMethodName, numArgs, mandatoryArgs, iterator != 0);
    MethodHandle target = MethodHandles.constant(Object.class, aProc);
    return new ConstantCallSite(target);
  }

  /** Private constructor to prevent instantiation. */
  private ConstantBuilder() {}
}

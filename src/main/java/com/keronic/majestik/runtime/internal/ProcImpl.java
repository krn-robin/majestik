/** */
package com.keronic.majestik.runtime.internal;

import module java.base;

import com.keronic.majestik.internal.Utils;
import com.keronic.majestik.runtime.Proc;

/** */
public class ProcImpl implements Proc {
  private static final System.Logger LOGGER =
      System.getLogger(MethodHandles.lookup().lookupClass().getName());

  public MethodHandle methodHandle;

  /**
   * Constructs a ProcImpl with the specified parameters.
   *
   * @param aClass the class containing the method
   * @param jMethodName the Java method name
   * @param magikMethodName the Magik method name
   * @param numArgs the number of arguments
   * @param mandatoryArgs the number of mandatory arguments
   * @param iterator indicates if the method is an iterator
   */
  @SuppressWarnings("java:S1172")
  public ProcImpl(
      Class<?> aClass,
      String jMethodName,
      String magikMethodName,
      int numArgs,
      int mandatoryArgs,
      boolean iterator) {
    LOGGER.log(
        System.Logger.Level.DEBUG,
        () ->
            String.format(
                "new ProcImpl(%s, %s, %s, %s, %s, %s)",
                aClass, jMethodName, magikMethodName, numArgs, mandatoryArgs, iterator));
    var mt =
        iterator
            ? MethodType.genericMethodType(numArgs - 2)
                .insertParameterTypes(0, MethodHandle.class, Object.class.arrayType())
            : MethodType.genericMethodType(numArgs);

    this.methodHandle = Utils.findStatic(aClass, jMethodName, mt);
  }

  public ProcImpl(
      Class<?> aClass, String jMethodName, String magikMethodName, int numArgs, int mandatoryArgs) {
    this(aClass, jMethodName, magikMethodName, numArgs, mandatoryArgs, false);
  }

  public ProcImpl(Class<?> aClass, String jMethodName, String magikMethodName, int numArgs) {
    this(aClass, jMethodName, magikMethodName, numArgs, numArgs);
  }

  public Object invoke(Object... arguments) throws Throwable {
    return methodHandle.invokeWithArguments(arguments);
  }
}

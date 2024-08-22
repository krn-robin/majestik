/** */
package com.keronic.majestik.runtime.internal;

import com.keronic.majestik.internal.Utils;
import com.keronic.majestik.runtime.Proc;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.logging.Logger;

/** */
public class ProcImpl implements Proc {
  private static final Logger LOGGER =
      Logger.getLogger(MethodHandles.lookup().lookupClass().getName());

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
    LOGGER.finest(
        () ->
            String.format(
                "new ProcImpl(%s, %s, %s, %s, %s, %s)",
                aClass, jMethodName, magikMethodName, numArgs, mandatoryArgs, iterator));
    var mt = MethodType.genericMethodType(numArgs);

    if (iterator) {
      mt = MethodType.genericMethodType(numArgs - 2);
      mt = mt.insertParameterTypes(0, MethodHandle.class, Object.class.arrayType());
    }

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

/** */
package com.keronic.majestik.runtime.internal;

import com.keronic.majestik.internal.Utils;
import com.keronic.majestik.runtime.Proc;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodType;

/** */
public class ProcImpl implements Proc {
	public MethodHandle methodHandle;

  public ProcImpl(
      Class<?> aClass,
      String jMethodName,
      String magikMethodName,
      int numArgs,
      int mandatoryArgs,
      boolean iterator) {
    this.methodHandle =
        Utils.findStatic(aClass, jMethodName, MethodType.genericMethodType(numArgs));
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

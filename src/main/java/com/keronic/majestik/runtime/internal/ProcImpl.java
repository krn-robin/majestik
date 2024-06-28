/**
 *
 */
package com.keronic.majestik.runtime.internal;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import com.keronic.majestik.runtime.Proc;

/**
 *
 */
public class ProcImpl implements Proc {
	public MethodHandle methodHandle;

	public ProcImpl(Class<?> aClass, String jMethodName, String magikMethodName, int numArgs, int mandatoryArgs, boolean iterator) throws NoSuchMethodException, IllegalAccessException {
		this.methodHandle = MethodHandles.lookup().findStatic(aClass, jMethodName,
				MethodType.genericMethodType(numArgs));
	}

	public ProcImpl(Class<?> aClass, String jMethodName, String magikMethodName, int numArgs, int mandatoryArgs) throws NoSuchMethodException, IllegalAccessException {
		this(aClass, jMethodName, magikMethodName, numArgs, mandatoryArgs, false);
	}
	public ProcImpl(Class<?> aClass, String jMethodName, String magikMethodName, int numArgs) throws NoSuchMethodException, IllegalAccessException {
		this(aClass, jMethodName, magikMethodName, numArgs, numArgs);
	}

    public Object invoke(Object ... arguments) throws Throwable {
		return methodHandle.invokeWithArguments(arguments);
    }
}

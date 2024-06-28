package com.keronic.majestik.language.invokers;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import com.keronic.majestik.runtime.Proc;

public class ConstantBuilder {
	public static CallSite stringBootstrap(MethodHandles.Lookup lookup, String name, MethodType type, String aString) {
    	var bla = MethodHandles.constant(Object.class, String.format(aString));
    	return new ConstantCallSite(bla);
    }

	public static CallSite procWithEmptyEnvBootstrap(MethodHandles.Lookup lookup, String name, MethodType type, Class<?> aClass, String jMethodName, String magikMethodName, int numArgs, int mandatoryArgs, int iterator, int primNo) throws Throwable {
		Proc aProc = Proc.of(aClass, jMethodName, magikMethodName, numArgs, mandatoryArgs, iterator != 0);
        MethodHandle target = MethodHandles.constant(Object.class, aProc);
        return new ConstantCallSite(target);
	}
}

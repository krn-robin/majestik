/**
 *
 */
package com.keronic.majestik.language.invokers;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 *
 */
public class GlobalAccessor {
	private final static Class<?> package_class = com.keronic.majestik.runtime.objects.Package.class;
	private static final MethodHandle getGlobal = getGlobal();

	private static MethodHandle getGlobal() {
		MethodHandle result = null;
		try {
			result = MethodHandles.lookup().findStatic(package_class, "get", MethodType.methodType(Object.class, String.class, String.class));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return result;
	}

    public static CallSite bootstrapFetcher2(MethodHandles.Lookup lookup, String name, MethodType type, String packageName, String global) {
        return new ConstantCallSite(MethodHandles.insertArguments(getGlobal, 0, packageName, global));
    }
}

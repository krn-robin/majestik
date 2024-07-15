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
public class DynamicAccessor {
	private static MethodHandle todo;

	public static void todo(Object o) {
		// TODO: implement lookup of dynamic variables
	}

    static {
    	try {
			todo = MethodHandles.lookup().findStatic(DynamicAccessor.class, "todo", MethodType.methodType(void.class, Object.class));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static CallSite bootstrapStorer(MethodHandles.Lookup lookup, String name, MethodType type, String packageName) {
		//System.out.format("\n\n--> %s, %s, %s, %s\n", lookup, name, type, packageName);
        return new ConstantCallSite(todo);
    }
}

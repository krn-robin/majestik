/**
 *
 */
package com.keronic.majestik.language.invokers;

import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import com.keronic.majestik.runtime.internal.ProcImpl;

/**
 *
 */
public class ProcInvoker {
	private static MethodHandle todo;

	public static Object todo(Object o1, Object o2) {
		ProcImpl proc = (ProcImpl) o1;
		try {
			// System.out.format("PROCINVOKE: %s %s\n", o1, o2);
			proc.invoke(o1, o2);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	static {
		try {
			todo = MethodHandles.lookup().findStatic(ProcInvoker.class, "todo", MethodType.genericMethodType(2));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static CallSite tupleBootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
		return new ConstantCallSite(todo);
	}

	public static CallSite naturalBootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
		return new ConstantCallSite(todo);
	}
}

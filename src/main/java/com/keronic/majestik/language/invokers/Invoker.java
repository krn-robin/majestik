/**
 *
 */
package com.keronic.majestik.language.invokers;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MutableCallSite;

import com.keronic.majestik.runtime.internal.ProcImpl;

/**
 *
 */
public class Invoker {
	private static MethodHandle todo;

    @SuppressWarnings("unused")
	public static Object todo(Object o) {
		MutableCallSite proccs = new MutableCallSite(MethodType.methodType(ProcImpl.class));
		ProcImpl proc = (ProcImpl)o;
		try {
			System.out.format("INVOKE: %s\n", o);
			proc.invoke(o);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

    static {
    	try {
			todo = MethodHandles.lookup().findStatic(Invoker.class, "todo", MethodType.genericMethodType(1));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public static CallSite tupleBootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
		System.out.format("name: %s %s %s\n", lookup, name, type);
//		new Exception().printStackTrace();
		return new MutableCallSite(todo);
    }
}

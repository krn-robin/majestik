/** */
package com.keronic.majestik.language.invokers;

import module java.base;

import com.keronic.majestik.internal.Utils;
import com.keronic.majestik.runtime.internal.ProcImpl;

/** */
public class Invoker {
  private static MethodHandle todo =
      Utils.findStatic(Invoker.class, "todo", MethodType.genericMethodType(1));

  /**
   * @param o
   * @return
   */
    @SuppressWarnings("unused")
	public static Object todo(Object o) {
		MutableCallSite proccs = new MutableCallSite(MethodType.methodType(ProcImpl.class));
    ProcImpl proc = (ProcImpl) o;
		try {
			System.out.format("INVOKE: %s\n", o);
			proc.invoke(o);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }

  /**
   * @param lookup
   * @param name
   * @param type
   * @return
   */
    public static CallSite tupleBootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
		System.out.format("name: %s %s %s\n", lookup, name, type);
		return new MutableCallSite(todo);
    }

  /** Private constructor to prevent instantiation. */
  private Invoker() {}
}

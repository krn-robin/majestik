/** */
package com.keronic.majestik.language.invokers;

import com.keronic.majestik.internal.Utils;
import com.keronic.majestik.runtime.internal.ProcImpl;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/** */
public class ProcInvoker {
  private static MethodHandle todo =
      Utils.findStatic(ProcInvoker.class, "todo", MethodType.genericMethodType(2));

  /**
   * @param o1
   * @param o2
   * @return
   */
	public static Object todo(Object o1, Object o2) {
		try {
			ProcImpl proc = (ProcImpl) o1;
			proc.invoke(o1, o2);
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
		return new ConstantCallSite(todo);
	}

  /**
   * @param lookup
   * @param name
   * @param type
   * @return
   */
  public static CallSite naturalBootstrap(
      MethodHandles.Lookup lookup, String name, MethodType type) {
		return new ConstantCallSite(todo);
	}

  /** */
  private ProcInvoker() {}
}

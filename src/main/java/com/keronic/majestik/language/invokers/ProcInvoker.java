/** */
package com.keronic.majestik.language.invokers;

import com.keronic.majestik.internal.Utils;
import com.keronic.majestik.runtime.internal.ProcImpl;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * ProcInvoker is responsible for invoking procedures and managing method handles for dynamic
 * invocation.
 */
public class ProcInvoker {
  private static MethodHandle todo =
      Utils.findStatic(ProcInvoker.class, "todo", MethodType.genericMethodType(2));

  /**
   * Invokes the procedure with the given objects.
   *
   * @param o1 the procedure object
   * @param o2 the argument object
   * @return the result of the invocation
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
   * Creates a call site for tuple operations.
   *
   * @param lookup the lookup context
   * @param name the name of the operation
   * @param type the method type
   * @return a constant call site for the operation
   */
	public static CallSite tupleBootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
		return new ConstantCallSite(todo);
	}

  /**
   * Creates a call site for natural operations.
   *
   * @param lookup the lookup context
   * @param name the name of the operation
   * @param type the method type
   * @return a constant call site for the operation
   */
  @SuppressWarnings("java:S1172")
  public static CallSite naturalBootstrap(
      MethodHandles.Lookup lookup, String name, MethodType type) {
		return new ConstantCallSite(todo);
	}

  public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
    return new ConstantCallSite(todo);
  }

  /** Private constructor to prevent instantiation. */
  private ProcInvoker() {}
}

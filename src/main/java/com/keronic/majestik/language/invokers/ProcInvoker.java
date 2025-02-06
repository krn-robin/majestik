/** */
package com.keronic.majestik.language.invokers;

import module java.base;

import com.keronic.majestik.internal.Utils;
import com.keronic.majestik.runtime.internal.ProcImpl;

/**
 * ProcInvoker is responsible for invoking procedures and managing method handles for dynamic
 * invocation.
 */
/**
 * ProcInvoker is responsible for invoking procedures and managing method handles for dynamic
 * invocation.
 *
 * This is implemented as an enum singleton to ensure thread-safety and prevent multiple instantiation.
 * Use {@link #INSTANCE} to access the singleton instance.
 */
public enum ProcInvoker {
  /** The singleton instance of the ProcInvoker. */
  INSTANCE;
}
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
			return proc.invoke(o1, o2);
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

  /**
   * Creates a call site for simple operations.
   *
   * @param lookup the lookup context
   * @param name the name of the operation
   * @param type the method type
   * @return a constant call site for the operation
   */
  @SuppressWarnings("java:S1172")
  public static CallSite bootstrap(MethodHandles.Lookup lookup, String name, MethodType type) {
    return new ConstantCallSite(todo);
  }
}

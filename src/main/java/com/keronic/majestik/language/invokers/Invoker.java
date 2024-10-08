/** */
package com.keronic.majestik.language.invokers;

import module java.base;
import com.keronic.majestik.MajestikRuntimeException;
import com.keronic.majestik.internal.Utils;
import com.keronic.majestik.runtime.internal.ProcImpl;

/** */
public class Invoker {
  private static final System.Logger LOGGER =
      System.getLogger(MethodHandles.lookup().lookupClass().getName());
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
      LOGGER.log(System.Logger.Level.DEBUG, () -> String.format("INVOKE: %s%n", o));
			proc.invoke(o);
		} catch (Throwable e) {
      throw new MajestikRuntimeException(e);
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
    LOGGER.log(
        System.Logger.Level.DEBUG, () -> String.format("name: %s %s %s%n", lookup, name, type));
		return new MutableCallSite(todo);
    }

  /** Private constructor to prevent instantiation. */
  private Invoker() {}
}

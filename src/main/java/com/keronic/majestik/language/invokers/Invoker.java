/** */
package com.keronic.majestik.language.invokers;

import module java.base;
import com.keronic.majestik.MajestikRuntimeException;
import com.keronic.majestik.internal.Utils;
import com.keronic.majestik.runtime.internal.ProcImpl;

/** */
public enum Invoker {
  INSTANCE;

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
      LOGGER.log(System.Logger.Level.DEBUG, () -> String.format("Invoking procedure: %s [id=%d] with type: %s on thread: %s",
          o,
          System.identityHashCode(o),
          o.getClass().getName(),
          Thread.currentThread().getName()));
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
        System.Logger.Level.DEBUG, () -> String.format("Bootstrap operation - name: %s, type: %s", name, type));
		return new MutableCallSite(todo);
    }
}

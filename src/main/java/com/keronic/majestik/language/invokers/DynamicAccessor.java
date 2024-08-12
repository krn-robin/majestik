/** */
package com.keronic.majestik.language.invokers;

import com.keronic.majestik.internal.Utils;
import java.lang.invoke.CallSite;
import java.lang.invoke.ConstantCallSite;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/** */
public class DynamicAccessor {
  private static MethodHandle todo =
      Utils.findStatic(
          DynamicAccessor.class, "todo", MethodType.methodType(void.class, Object.class));

  /**
   * @param o
   */
	public static void todo(Object o) {
		// TODO: implement lookup of dynamic variables
	}

  /**
   * @param lookup
   * @param name
   * @param type
   * @param packageName
   * @return
   */
  @SuppressWarnings("java:S1172")
  public static CallSite bootstrapStorer(
      MethodHandles.Lookup lookup, String name, MethodType type, String packageName) {
    // System.out.format("\n\n--> %s, %s, %s, %s\n", lookup, name, type, packageName);
        return new ConstantCallSite(todo);
    }

  /** */
  private DynamicAccessor() {}
}

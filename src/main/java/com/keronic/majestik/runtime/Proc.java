/** */
package com.keronic.majestik.runtime;

import com.keronic.majestik.runtime.internal.ProcImpl;

/** */
public interface Proc {
  static Proc of(
      Class<?> aClass,
      String jMethodName,
      String magikMethodName,
      int numArgs,
      int mandatoryArgs,
      boolean iterator)
      throws NoSuchMethodException, IllegalAccessException {
    return new ProcImpl(aClass, jMethodName, magikMethodName, numArgs, mandatoryArgs, iterator);
  }
}

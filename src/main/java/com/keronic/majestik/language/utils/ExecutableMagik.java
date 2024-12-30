/** */
package com.keronic.majestik.language.utils;

/** */
public interface ExecutableMagik {
  Object execute();

  default void preload() {}
}

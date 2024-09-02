/** */
package com.keronic.majestik.language.utils;

/** */
public interface ExecutableMagik {
	public Object execute();

  public default void preload() {}
}

/** */
package com.keronic.majestik.language;

import java.util.Arrays;

/** */
public class ResultTuple {
	public static final ResultTuple EMPTY_TUPLE = new ResultTuple();
	private Object[] objects;

	ResultTuple(Object... objects) {
		this.objects = objects;
	}

  public static ResultTuple create(Object... objects) {
    if (objects.length > 0) return new ResultTuple(objects);
    else return EMPTY_TUPLE;
  }

  public int size() {
    return this.objects.length;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof ResultTuple that) return Arrays.deepEquals(this.objects, that.objects);
	  return false;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(objects);
  }
}

/**
 *
 */
package com.keronic.majestik.language;

/**
 *
 */
public class ResultTuple {
	public static final ResultTuple EMPTY_TUPLE = new ResultTuple();
	private Object[] objects;

	ResultTuple(Object... objects) {
		this.objects = objects;
	}
}

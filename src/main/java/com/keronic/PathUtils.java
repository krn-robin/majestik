/**
 *
 */
package com.keronic;

/**
 *
 */
public class PathUtils {

	/**
	 *
	 */
	public static String getBaseName(String filename) {
		return filename.replaceAll("(?<=.)\\.[^.]+$", "");
	}
}

/**
 *
 */
package com.keronic.majestik.runtime.objects;

import com.keronic.majestik.runtime.objects.internal.PackageImpl;

/**
 *
 */
public interface Package {
	static Object get(String packageName, String variableName) {
		return PackageImpl.get(packageName, variableName);
	}

	static Object put(String packageName, String variableName, Object o) {
		return PackageImpl.put(packageName, variableName, o);
	}
}

/** */
package com.keronic.majestik.runtime.objects;

import com.keronic.majestik.runtime.objects.internal.PackageImpl;

/** */
public interface Package {
  static Object get(String packageName, String variableName) {
    return PackageImpl.get(packageName, variableName);
  }

  static void put(String packageName, String variableName, Object o) {
    PackageImpl.put(packageName, variableName, o);
  }
}

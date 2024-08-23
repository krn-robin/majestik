/** */
package com.keronic.majestik.runtime.objects.internal;

import com.keronic.majestik.runtime.objects.Package;
import java.lang.invoke.MethodHandles;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/** */
public class PackageImpl implements Package {
  private static final Logger LOGGER =
      Logger.getLogger(MethodHandles.lookup().lookupClass().getName());
  private static final Map<String, Package> all_packages = new ConcurrentHashMap<>();
  private final Map<String, Object> variables = new ConcurrentHashMap<>();
	protected final String name;
	protected final Package[] parents;
  private static final Package SW_PACKAGE = new PackageImpl("sw");
  private static final Package USER_PACKAGE = new PackageImpl("user", new Package[] {SW_PACKAGE});

	public PackageImpl(String name, Package[] parents) {
		this.name = name;
		this.parents = parents;
		PackageImpl.all_packages.put(name, this);
	}

	public PackageImpl(String name) {
		this(name, new Package[0]);
	}

  public static Object get(String packageName, String variableName) {
    PackageImpl p = (PackageImpl) PackageImpl.all_packages.get(packageName);
		return p.get(variableName);
	}

  public static void put(String packageName, String variableName, Object o) {
    PackageImpl p = (PackageImpl) PackageImpl.all_packages.get(packageName);
    p.put(variableName, o);
	}

	private Object get(String variableName) {
    LOGGER.finest(() -> String.format("PackageImpl.get(%s, %s)", this.name, variableName));
		return this.variables.get(variableName);
	}

  private void put(String variableName, Object o) {
    LOGGER.finest(() -> String.format("PackageImpl.put(%s, %s, %s)", this.name, variableName, o));
    this.variables.put(variableName, o);
	}
}

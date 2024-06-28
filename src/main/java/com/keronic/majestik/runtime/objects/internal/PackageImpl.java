/**
 *
 */
package com.keronic.majestik.runtime.objects.internal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.keronic.majestik.runtime.objects.Package;

/**
 *
 */
public class PackageImpl implements Package {
	private final static Map<String,Package> all_packages = new ConcurrentHashMap<String,Package>();
	private final Map<String,Object> variables = new ConcurrentHashMap<String,Object>();
	protected final String name;
	protected final Package[] parents;
	private final static Package SW_PACKAGE = new PackageImpl("sw");
	private final static Package USER_PACKAGE = new PackageImpl("user", new Package[] { SW_PACKAGE });

	public PackageImpl(String name, Package[] parents) {
		this.name = name;
		this.parents = parents;
		PackageImpl.all_packages.put(name, this);
	}

	public PackageImpl(String name) {
		this(name, new Package[0]);
	}

	static public Object get(String packageName, String variableName) {
		PackageImpl p = (PackageImpl)PackageImpl.all_packages.get(packageName);
		return p.get(variableName);
	}

	static public Object put(String packageName, String variableName, Object o) {
		PackageImpl p = (PackageImpl)PackageImpl.all_packages.get(packageName);
		return p.put(variableName, o);
	}

	private Object get(String variableName) {
		//System.out.format("PackageImpl.get(%s, %s)\n", this.name, variableName); // DEBUG
		return this.variables.get(variableName);
	}

	private Object put(String variableName, Object o) {
		//System.out.format("PackageImpl.put(%s, %s)\n", this.name, variableName); // DEBUG
		return this.variables.put(variableName, o);
	}
}

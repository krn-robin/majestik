/** */
package com.keronic;

import module java.base;

/** */
public class MajestikClassLoader extends ClassLoader {

  static final Function<ClassDesc, ClassDesc> REMAP_FUNCTION =
      cd ->
          ClassDesc.of(
              cd.packageName().replace("com.gesmallworld.magik", "com.keronic.majestik"),
              cd.displayName());

	public MajestikClassLoader() {
		// TODO Auto-generated constructor stub
		super("Majestik", getSystemClassLoader());
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
    if (name.startsWith("magik") || name.startsWith("majestik")) {
			try {
				var cd = this.loadClassData(name);
				return super.defineClass(name, cd, 0, cd.length);
			} catch (IOException e) {
		    return super.findClass(name);
			}
		}
		return super.findClass(name);
	}

	private byte[] loadClassData(String className) throws IOException {
    var fileName = className.replace('.', File.separatorChar) + ".class";

    var cf = ClassFile.of(ClassFile.ConstantPoolSharingOption.NEW_POOL);
    var cm = cf.parse(Path.of(fileName));
		var crm = ClassRemapper.of(REMAP_FUNCTION);

    // Transform the class file, applying the REMAP_FUNCTION to update package names
    return cf.transform(cm, crm);
	}
}

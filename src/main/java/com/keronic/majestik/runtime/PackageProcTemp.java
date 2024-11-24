package com.keronic.majestik.runtime;

import com.keronic.majestik.runtime.internal.ProcImpl;
import com.keronic.majestik.runtime.objects.Package;

public class PackageProcTemp extends ProcImpl {
  public static Object write(Object self, Object arg) {
    // System.out.format("WriteProcTemp(\"%s\")\n", arg);
    return null;
  }

  public PackageProcTemp() throws NoSuchMethodException, IllegalAccessException {
    super(PackageProcTemp.class, "package", "package", 2);
  }

  static {
    try {
      Package.put("sw", "package", (Object) new WriteProcTemp());
    } catch (NoSuchMethodException | IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
}

package com.keronic.majestik.runtime;

import com.keronic.majestik.runtime.objects.Package;
import com.keronic.majestik.runtime.internal.ProcImpl;

public class WriteProcTemp extends ProcImpl {
	static public Object write(Object self, Object arg) {
		//System.out.format("WriteProcTemp(\"%s\")\n", arg);
		System.out.println(arg);
		return null;
	}

	public WriteProcTemp() throws NoSuchMethodException, IllegalAccessException {
		super(WriteProcTemp.class, "write", "write", 2);
	}

	static {
		try {
			Package.put("sw", "write", (Object)new WriteProcTemp());
		} catch (NoSuchMethodException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

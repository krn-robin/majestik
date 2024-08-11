package com.keronic.language.invokers.test;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static org.junit.Assert.*;

import com.keronic.majestik.constant.ConstantDescs;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Consumer;
import org.junit.Test;

public class GlobalAccessorTest {
  @Test
  public void testGlobalFetch() throws Throwable {
    var mt = MethodType.genericMethodType(0);
    var mtd = mt.describeConstable().get();
    var classname = Class.forName("com.keronic.majestik.runtime.WriteProcTemp").getSimpleName();

    Consumer<CodeBuilder> cb =
	xb -> {
	  xb.invokedynamic(
			DynamicCallSiteDesc.of(ConstantDescs.BSM_GLOBAL_FETCHER, "fetch", ConstantDescs.MTD_Object, "sw", "write"));
    //	  xb.checkcast(ConstantDescs.CD_Long);
	  xb.areturn();
	};

    var bytes =
	ClassFile.of()
	    .build(
		ClassDesc.of("com.keronic.language.invokers.test.C"),
		clb -> {
		  clb.withMethodBody("m", mtd, ACC_PUBLIC | ACC_STATIC, cb);
		});

    var lookup = MethodHandles.lookup().defineHiddenClass(bytes, true);
    var m = lookup.findStatic(lookup.lookupClass(), "m", mt);
    System.out.println(m.invoke());
    assertNotNull(m.invoke());
    assertEquals(m.invoke().getClass().getSimpleName(), classname);
  }
}

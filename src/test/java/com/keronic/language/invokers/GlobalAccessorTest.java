package com.keronic.language.invokers;

import module java.base;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static org.junit.jupiter.api.Assertions.*;

import com.keronic.majestik.constant.ConstantDescs;
import org.junit.jupiter.api.Test;

/** */
class GlobalAccessorTest {
  /**
   * @throws Throwable
   */
  @Test
  void testGlobalStoreAndFetch() throws Throwable {
    var mt = MethodType.genericMethodType(0);
    var mtd = mt.describeConstable().get();

    Consumer<CodeBuilder> cb =
	xb -> {
          xb.ldc((long) 54321);
          xb.invokestatic(ConstantDescs.CD_Long, "valueOf", ConstantDescs.MTD_Longlong);
          xb.invokedynamic(
              DynamicCallSiteDesc.of(
                  ConstantDescs.BSM_GLOBAL_STORER,
                  "store",
                  ConstantDescs.MTD_voidObject,
                  "user",
                  "test"));
	  xb.invokedynamic(
              DynamicCallSiteDesc.of(
                  ConstantDescs.BSM_GLOBAL_FETCHER,
                  "fetch",
                  ConstantDescs.MTD_Object,
                  "user",
                  "test"));
	  xb.areturn();
	};

    var bytes =
	ClassFile.of()
	    .build(
                ClassDesc.of(this.getClass().getPackageName() + ".C"),
		clb -> {
		  clb.withMethodBody("m", mtd, ACC_PUBLIC | ACC_STATIC, cb);
		});

    var lookup = MethodHandles.lookup().defineHiddenClass(bytes, true);
    var m = lookup.findStatic(lookup.lookupClass(), "m", mt);
    assertEquals(Long.valueOf(54321), m.invoke());
  }
}

package com.keronic.majestik.language.invokers;

import module java.base;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.keronic.majestik.constant.ConstantDescs;
import org.junit.jupiter.api.Test;

/** Tests for the GlobalAccessor class which handles global variable storage and retrieval. */
class GlobalAccessorTest {
  private static final String TEST_PACKAGE = GlobalAccessorTest.class.getPackageName();
  private static final String TEST_CLASS = TEST_PACKAGE + ".C";

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
                ClassDesc.of(TEST_CLASS),
                clb -> {
                  clb.withMethodBody("m", mtd, ACC_PUBLIC | ACC_STATIC, cb);
                });

    var lookup = MethodHandles.lookup().defineHiddenClass(bytes, true);
    var m = lookup.findStatic(lookup.lookupClass(), "m", mt);
    assertEquals(Long.valueOf(54321), m.invoke());
  }
}

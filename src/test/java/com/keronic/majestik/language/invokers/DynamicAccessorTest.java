package com.keronic.majestik.language.invokers;

import module java.base;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static org.junit.jupiter.api.Assertions.*;

import com.keronic.majestik.constant.ConstantDescs;
import org.junit.jupiter.api.Test;

class DynamicAccessorTest {
  private static final String TEST_PACKAGE = DynamicAccessorTest.class.getPackageName();
  private static final String TEST_CLASS = TEST_PACKAGE + ".C";

  /**
   * @throws Throwable
   */
  @Test
  void testStoreDynamic() throws Throwable {
    var mt = MethodType.methodType(Object.class);
    var mtd = mt.describeConstable().get();
    var globalname = "!_test_!";
    var packagename = "user";
    long value = 19791012;

    Consumer<CodeBuilder> cb =
        xb -> {
          xb.ldc(value);
          xb.invokestatic(ConstantDescs.CD_Long, "valueOf", ConstantDescs.MTD_Longlong);
          xb.invokedynamic(
              DynamicCallSiteDesc.of(
                  ConstantDescs.BSM_DYNAMIC_STORER,
                  globalname,
                  ConstantDescs.MTD_voidObject,
                  packagename));
          xb.aconst_null();
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
    assertNull(m.invoke()); // TODO: Check the actual stored value
  }
}

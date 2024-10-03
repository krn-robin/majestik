package com.keronic.majestik.language.invokers;

import module java.base;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static org.junit.jupiter.api.Assertions.*;

import com.keronic.majestik.constant.ConstantDescs;
import com.keronic.majestik.runtime.internal.ProcImpl;
import org.junit.jupiter.api.Test;

public class ConstantBuilderTest {
  private static final String TEST_PACKAGE = ConstantBuilderTest.class.getPackageName();
  private static final String TEST_CLASS = TEST_PACKAGE + ".C";

  public static Object testM() {
    return Integer.valueOf(12345);
  }

  /**
   * @throws Throwable
   */
  @Test
  void testStringBuilder() throws Throwable {
    var mt = MethodType.methodType(String.class);
    var mtd = mt.describeConstable().get();
    var str = "a_test";

    Consumer<CodeBuilder> cb =
        xb -> {
          xb.invokedynamic(
              DynamicCallSiteDesc.of(
                  ConstantDescs.BSM_STRING_BUILDER, "string", ConstantDescs.MTD_Object, str));
          xb.checkcast(ConstantDescs.CD_String);
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
    assertEquals(str, m.invoke());
  }

  /**
   * @throws Throwable
   */
  @Test
  void testProcWithEmptyEnvBootstrap() throws Throwable {
    var mt = MethodType.methodType(ProcImpl.class);
    var mtd = mt.describeConstable().get();
    var classname = ClassDesc.of(this.getClass().getName());

    Consumer<CodeBuilder> cb =
        xb -> {
          xb.invokedynamic(
              DynamicCallSiteDesc.of(
                  ConstantDescs.BSM_PROC_EMPTY_ENV_LITERAL,
                  "proc",
                  ConstantDescs.MTD_Object,
                  classname,
                  "testM",
                  "test_method",
                  0,
                  0,
                  0,
                  0));
          xb.checkcast(ClassDesc.of(ProcImpl.class.getName()));
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
    var result = (ProcImpl) m.invoke();
    assertNotNull(result);
    assertEquals(12345, result.invoke());
  }
}

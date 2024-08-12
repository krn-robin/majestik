package com.keronic.language.invokers.test;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static org.junit.Assert.*;

import com.keronic.majestik.constant.ConstantDescs;
import com.keronic.majestik.runtime.internal.ProcImpl;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Consumer;
import org.junit.Test;

public class ConstantBuilderTest {
  /**
   * @throws Throwable
   */
  @Test
  public void testStringBuilder() throws Throwable {
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
                ClassDesc.of("com.keronic.language.invokers.test.C"),
                clb -> {
                  clb.withMethodBody("m", mtd, ACC_PUBLIC | ACC_STATIC, cb);
                });

    var lookup = MethodHandles.lookup().defineHiddenClass(bytes, true);
    var m = lookup.findStatic(lookup.lookupClass(), "m", mt);
    assertEquals(str, m.invoke());
  }

  public static Object testM() {
    return Integer.valueOf(12345);
  }

  /**
   * @throws Throwable
   */
  @Test
  public void testProcWithEmptyEnvBootstrap() throws Throwable {
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
                ClassDesc.of("com.keronic.language.invokers.test.C"),
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

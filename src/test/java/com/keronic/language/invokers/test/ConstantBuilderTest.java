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

public class ConstantBuilderTest {
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
    assertEquals(m.invoke(), str);
  }
}

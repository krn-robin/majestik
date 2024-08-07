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

public class BinaryDispatcherTest {
  @Test
  public void testLongPlusLong() throws Throwable {
    var mt = MethodType.methodType(Long.class, Long.class, Long.class);
    var mtd = mt.describeConstable().get();
    long l1 = 1, l2 = 2, l3 = 3;

    Consumer<CodeBuilder> cb =
        xb -> {
          xb.constantInstruction(l1);
          xb.invokestatic(ConstantDescs.CD_Long, "valueOf", ConstantDescs.MTD_Longlong);
          xb.constantInstruction(l2);
          xb.invokestatic(ConstantDescs.CD_Long, "valueOf", ConstantDescs.MTD_Longlong);
          xb.invokedynamic(
              DynamicCallSiteDesc.of(
                  ConstantDescs.BSM_BINARY_DISPATCHER,
                  "+",
                  MethodType.genericMethodType(2).describeConstable().get()));
          xb.checkcast(ConstantDescs.CD_Long);
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
    assertEquals(m.invoke(l1, l2), l3);
  }
}

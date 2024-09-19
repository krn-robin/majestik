package com.keronic.language.invokers;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static org.junit.jupiter.api.Assertions.*;

import com.keronic.majestik.constant.ConstantDescs;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;

/** */
class ProcInvokerTest {
  /**
   * @throws Throwable
   */
  @Test
  void testNaturalBootstrap() throws Throwable {
    var mt = MethodType.methodType(void.class);
    var mtd = mt.describeConstable().get();
    Class.forName("com.keronic.majestik.runtime.WriteProcTemp").getSimpleName();

    Consumer<CodeBuilder> cb =
        xb -> {
          xb.invokedynamic(
              DynamicCallSiteDesc.of(
                  ConstantDescs.BSM_GLOBAL_FETCHER,
                  "fetch",
                  ConstantDescs.MTD_Object,
                  "sw",
                  "write"));
      xb.ldc("test");
          xb.invokedynamic(
              DynamicCallSiteDesc.of(
                  ConstantDescs.BSM_NATURAL_PROC, "()", ConstantDescs.MTD_ObjectObjectObject));
       xb.return_();
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
    assertNull(m.invoke()); // TODO: Check the actual invoked procedure
  }
}

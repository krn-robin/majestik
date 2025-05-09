package com.keronic.majestik.language.invokers;

import module java.base;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.keronic.majestik.constant.ConstantDescs;
import com.keronic.majestik.language.ResultTuple;
import org.junit.jupiter.api.Test;

/** */
class TupleBuilderTest {
  private static final String TEST_PACKAGE = TupleBuilderTest.class.getPackageName();
  private static final String TEST_CLASS = TEST_PACKAGE + ".C";

  /**
   * Tests the dynamic creation of a `ResultTuple` using the `BSM_TUPLE_BUILDER` bootstrap method.
   *
   * <p>This test dynamically generates a method that returns a `ResultTuple` containing a single
   * string element. The method is defined in a hidden class and invoked to verify that the
   * `ResultTuple` is correctly created with the expected value and size.
   *
   * <p>The test ensures that:<br>
   * 1. The dynamic method invocation correctly uses the `BSM_TUPLE_BUILDER` bootstrap method.<br>
   * 2. The returned `ResultTuple` contains the string "MagicString".<br>
   * 3. The size of the `ResultTuple` is 1.<br>
   *
   * @throws Throwable if any error occurs during method generation or invocation.
   */
  @Test
  void testBootstrap() throws Throwable {
    var mt = MethodType.methodType(ResultTuple.class);
    var mtd = mt.describeConstable().get();
    var magic = "MagicString";

    Consumer<CodeBuilder> cb =
        xb -> {
          xb.ldc(new String(magic));
          xb.invokedynamic(
              DynamicCallSiteDesc.of(
                  ConstantDescs.BSM_TUPLE_BUILDER,
                  "tuplebuilder",
                  ConstantDescs.MTD_ResultTupleObject));
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

    Object result = m.invoke();
    assertEquals(ResultTuple.create(magic), result);
    assertEquals(1, ((ResultTuple) result).size());
  }
}

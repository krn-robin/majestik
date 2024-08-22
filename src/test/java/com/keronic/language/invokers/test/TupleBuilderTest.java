package com.keronic.language.invokers.test;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static org.junit.Assert.*;

import com.keronic.majestik.constant.ConstantDescs;
import com.keronic.majestik.language.ResultTuple;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Consumer;
import org.junit.Test;

/** */
public class TupleBuilderTest {
  /**
   * @throws Throwable
   */
  @Test
  public void testBootstrap() throws Throwable {
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
		ClassDesc.of("com.keronic.language.invokers.test.C"),
		clb -> {
		  clb.withMethodBody("m", mtd, ACC_PUBLIC | ACC_STATIC, cb);
		});

    var lookup = MethodHandles.lookup().defineHiddenClass(bytes, true);
    var m = lookup.findStatic(lookup.lookupClass(), "m", mt);

    Object result = m.invoke();
    assertEquals(ResultTuple.create(magic), result);
    assertEquals(1, ((ResultTuple)result).size());
  }
}

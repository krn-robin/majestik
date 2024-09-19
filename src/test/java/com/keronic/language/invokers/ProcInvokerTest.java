package com.keronic.language.invokers;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static java.lang.constant.ConstantDescs.INIT_NAME;
import static org.junit.jupiter.api.Assertions.*;

import com.keronic.majestik.constant.ConstantDescs;
import com.keronic.majestik.runtime.objects.Package;
import java.lang.classfile.ClassFile;
import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.function.Consumer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** */
class ProcInvokerTest {

  /**
   * Sets up the test environment before each test case. This method dynamically constructs a class
   * that extends `ProcImpl` and registers it in the package.
   *
   * @throws Throwable if an error occurs during setup.
   */
  @BeforeEach
  void setUp() throws Throwable {
    var cd_t = ClassDesc.of(this.getClass().getPackageName() + ".T");
    var cd_p = ClassDesc.of("com.keronic.majestik.runtime.internal.ProcImpl");
    var tbytes =
        ClassFile.of()
            .build(
                cd_t,
                clb ->
                    clb.withSuperclass(cd_p)
                        .withMethodBody(
                            "run",
                            MethodType.genericMethodType(2).describeConstable().get(),
                            ACC_PUBLIC | ACC_STATIC,
                            cb -> cb.aload(1).areturn())
                        .withMethodBody(
                            INIT_NAME,
                            ConstantDescs.MTD_void,
                            ClassFile.ACC_PUBLIC,
                            cb ->
                                cb.aload(0)
                                    .loadConstant(cd_t)
                                    .loadConstant("run")
                                    .loadConstant("run_test")
                                    .loadConstant(2)
                                    .invokespecial(
                                        cd_p, INIT_NAME, ConstantDescs.MTD_voidClassStringStringint)
                                    .return_()));
    var tlookup = MethodHandles.lookup().defineHiddenClass(tbytes, true);
    Package.put("sw", "run_test", tlookup.lookupClass().getDeclaredConstructor().newInstance());
  }

  /**
   * Cleans up resources after each test by removing the dynamically registered class from the
   * package.
   *
   * @throws Throwable if an error occurs during teardown.
   */
  @AfterEach
  void tearDown() throws Throwable {
    Package.put("sw", "run_test", null);
  }

  /**
   * Tests the dynamic invocation using a bootstrap method. This test sets up a class `C` that
   * dynamically invokes the method `run_test` from class `T` using `invokedynamic`. It verifies
   * that the expected result "succeed" is returned, ensuring that dynamic method resolution and
   * invocation are functioning correctly.
   *
   * @throws Throwable if any exception occurs during the test.
   */
  @Test
  void testNaturalBootstrap() throws Throwable {
    var mt = MethodType.methodType(Object.class);
    var mtd = mt.describeConstable().get();

    //  ClassBuilder

    Consumer<CodeBuilder> cb =
        xb -> {
          xb.invokedynamic(
              DynamicCallSiteDesc.of(
                  ConstantDescs.BSM_GLOBAL_FETCHER,
                  "fetch",
                  ConstantDescs.MTD_Object,
                  "sw",
                  "run_test"));
          xb.ldc("succeed");
          xb.invokedynamic(
              DynamicCallSiteDesc.of(
                  ConstantDescs.BSM_NATURAL_PROC, "()", ConstantDescs.MTD_ObjectObjectObject));
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

    // Verify TestProc was actually called.
    assertEquals("succeed", m.invoke());
  }
}

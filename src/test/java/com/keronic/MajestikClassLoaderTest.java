package com.keronic;

import module java.base;

import static java.lang.constant.ConstantDescs.INIT_NAME;
import static org.junit.jupiter.api.Assertions.*;

import com.keronic.majestik.constant.ConstantDescs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MajestikClassLoaderTest {
  @BeforeEach
  void setUp() throws Throwable {
    var dyndesc =
        DynamicCallSiteDesc.of(
            java.lang.constant.ConstantDescs.ofCallsiteBootstrap(
                ClassDesc.of("com.gesmallworld.magik.language.invokers.ConstantBuilder"),
                "stringBootstrap",
                ConstantDescs.CD_CallSite,
                ConstantDescs.CD_String),
            "string",
            ConstantDescs.MTD_Object,
            "test");

    Files.createDirectories(Path.of("magik"));

    ClassFile.of()
        .buildTo(
            Path.of("magik/T.class"),
            ClassDesc.of("magik.T"),
            clb ->
                clb.withMethodBody(
                        INIT_NAME,
                        ConstantDescs.MTD_void,
                        ClassFile.ACC_PUBLIC,
                        cb ->
                            cb.aload(0)
                                .invokespecial(
                                    ConstantDescs.CD_Object, INIT_NAME, ConstantDescs.MTD_void)
                            .invokedynamic(dyndesc)
                            .return_()));
  }

  @AfterEach
  void tearDown() throws Throwable {
    Files.deleteIfExists(Path.of("magik/T.class"));
  }

  @Test
  void testFindClass() throws Throwable {
    MajestikClassLoader classLoader = new MajestikClassLoader();

    // Test with a valid class name
    Class<?> validClass = classLoader.findClass("magik.T");
    assertNotNull(validClass);
    assertEquals("magik.T", validClass.getName());
    validClass.getConstructor().newInstance();

    // Test with an invalid class name
    assertThrows(
        ClassNotFoundException.class,
        () -> {
          classLoader.findClass("magik.Q");
        });
  }
}

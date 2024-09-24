package com.keronic;

import module java.base;

import static org.junit.jupiter.api.Assertions.*;

import com.keronic.majestik.constant.ConstantDescs;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MajestikClassLoaderTest {
  private static final Path MAGIK_DIR = Path.of("magik");
  private static final Path T_CLASS_PATH = MAGIK_DIR.resolve("T.class");

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

    Files.createDirectories(MAGIK_DIR);

    ClassFile.of()
        .buildTo(
            T_CLASS_PATH,
            ClassDesc.of("magik.T"),
            clb ->
                clb.withMethodBody(
                    ConstantDescs.INIT_NAME,
                        ConstantDescs.MTD_void,
                        ClassFile.ACC_PUBLIC,
                        cb ->
                            cb.aload(0)
                                .invokespecial(
                                ConstantDescs.CD_Object,
                                ConstantDescs.INIT_NAME,
                                ConstantDescs.MTD_void)
                            .invokedynamic(dyndesc)
                            .return_()));
  }

  @AfterEach
  void tearDown() throws Throwable {
    Files.deleteIfExists(T_CLASS_PATH);
  }

  @Test
  void testFindClass() throws Throwable {
    MajestikClassLoader classLoader = new MajestikClassLoader();

    // Test with a valid class name
    Class<?> validClass = classLoader.findClass("magik.T");
    assertNotNull(validClass);
    assertEquals("magik.T", validClass.getName());
    validClass.getConstructor().newInstance();

    // Test with invalid class names
    assertThrows(
        ClassNotFoundException.class,
        () -> {
          classLoader.findClass("magik.Q.NonExistingClass");
        });
    assertThrows(
	    ClassNotFoundException.class,
	    () -> {
	      classLoader.findClass("com.example.NonExistingClass");
        });
  }
}

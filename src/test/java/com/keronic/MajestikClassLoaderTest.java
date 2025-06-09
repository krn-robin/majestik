package com.keronic;

import module java.base;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keronic.majestik.constant.ConstantDescs;
import java.lang.ClassLoader;
import java.lang.classfile.AccessFlag;
import java.lang.classfile.ClassFile;
import java.lang.classfile.attribute.ConstantValueAttribute;
import java.lang.constant.ClassDesc;
import java.lang.constant.MethodTypeDesc;
import java.lang.invoke.TypeDescriptor;
import java.lang.invoke.constant.DynamicCallSiteDesc;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MajestikClassLoaderTest {
  private static final Path MAGIK_ROOT_DIR = Path.of("target", "test-generated-classes", "majestik");
  private static final Path T_CLASS_PATH = MAGIK_ROOT_DIR.resolve("T.class");
  private static final Path GW_MAGIK_PACKAGE_DIR = MAGIK_ROOT_DIR.resolve("com/gesmallworld/magik");
  private static final Path ORIGINAL_TEST_CLASS_PATH = GW_MAGIK_PACKAGE_DIR.resolve("OriginalTestClass.class");
  private static final Path ANOTHER_CLASS_PATH = GW_MAGIK_PACKAGE_DIR.resolve("AnotherClass.class");
  private static final Path YET_ANOTHER_CLASS_PATH = GW_MAGIK_PACKAGE_DIR.resolve("YetAnotherClass.class");

  @BeforeEach
  void setUp() throws Throwable {
    var dyndesc =
        DynamicCallSiteDesc.of(
            java.lang.constant.ConstantDescs.ofCallsiteBootstrap(
                java.lang.constant.ClassDesc.of("com.gesmallworld.magik.language.invokers.ConstantBuilder"),
                "stringBootstrap",
                com.keronic.majestik.constant.ConstantDescs.CD_CallSite,
                com.keronic.majestik.constant.ConstantDescs.CD_String),
            "string",
            com.keronic.majestik.constant.ConstantDescs.MTD_Object,
            "test");

    Files.createDirectories(MAGIK_ROOT_DIR);
    Files.createDirectories(GW_MAGIK_PACKAGE_DIR);

    ClassFile.of()
        .buildTo(
            T_CLASS_PATH,
            java.lang.constant.ClassDesc.of("magik.T"),
            clb ->
                clb.withMethodBody(
                    com.keronic.majestik.constant.ConstantDescs.INIT_NAME,
                    com.keronic.majestik.constant.ConstantDescs.MTD_void,
                    ClassFile.ACC_PUBLIC,
                    cb ->
                        cb.aload(0)
                            .invokespecial(
                                com.keronic.majestik.constant.ConstantDescs.CD_Object,
                                com.keronic.majestik.constant.ConstantDescs.INIT_NAME,
                                com.keronic.majestik.constant.ConstantDescs.MTD_void)
                            .invokedynamic(dyndesc)
                            .return_()));

    // Generate com.gesmallworld.magik.AnotherClass
    ClassDesc cdAnotherClass = java.lang.constant.ClassDesc.of("com.gesmallworld.magik.AnotherClass");
    ClassFile.of().buildTo(ANOTHER_CLASS_PATH, cdAnotherClass, clb -> {
        clb.withFlags(AccessFlag.PUBLIC, AccessFlag.SUPER);
        clb.withMethodBody(java.lang.constant.ConstantDescs.INIT_NAME, java.lang.constant.ConstantDescs.MTD_void, ClassFile.ACC_PUBLIC, cb -> cb
            .aload(0)
            .invokespecial(java.lang.constant.ConstantDescs.CD_Object, java.lang.constant.ConstantDescs.INIT_NAME, java.lang.constant.ConstantDescs.MTD_void)
            .return_());
    });

    // Generate com.gesmallworld.magik.YetAnotherClass
    ClassDesc cdYetAnotherClass = java.lang.constant.ClassDesc.of("com.gesmallworld.magik.YetAnotherClass");
    ClassFile.of().buildTo(YET_ANOTHER_CLASS_PATH, cdYetAnotherClass, clb -> {
        clb.withFlags(AccessFlag.PUBLIC, AccessFlag.SUPER);
        clb.withMethodBody(java.lang.constant.ConstantDescs.INIT_NAME, java.lang.constant.ConstantDescs.MTD_void, ClassFile.ACC_PUBLIC, cb -> cb
            .aload(0)
            .invokespecial(java.lang.constant.ConstantDescs.CD_Object, java.lang.constant.ConstantDescs.INIT_NAME, java.lang.constant.ConstantDescs.MTD_void)
            .return_());
    });

    // Generate com.gesmallworld.magik.OriginalTestClass
    ClassDesc cdOriginalTestClass = java.lang.constant.ClassDesc.of("com.gesmallworld.magik.OriginalTestClass");
    ClassFile.of().buildTo(ORIGINAL_TEST_CLASS_PATH, cdOriginalTestClass, clb -> {
        clb.withFlags(AccessFlag.PUBLIC, AccessFlag.SUPER);

        // Default Constructor
        clb.withMethodBody(java.lang.constant.ConstantDescs.INIT_NAME, java.lang.constant.ConstantDescs.MTD_void, ClassFile.ACC_PUBLIC, cb -> cb
            .aload(0)
            .invokespecial(java.lang.constant.ConstantDescs.CD_Object, java.lang.constant.ConstantDescs.INIT_NAME, java.lang.constant.ConstantDescs.MTD_void)
            .return_());

        // String Constant Field: public static final String GREETING = "Original package: com.gesmallworld.magik";
        clb.withField("GREETING", java.lang.constant.ConstantDescs.CD_String,
            ClassFile.ACC_PUBLIC | ClassFile.ACC_STATIC | ClassFile.ACC_FINAL,
            fb -> fb.withAttribute(ConstantValueAttribute.of(
                clb.constantPool().stringEntry("Original package: com.gesmallworld.magik"))
            ));

        // Field of type AnotherClass: public AnotherClass myField;
        clb.withField("myField", cdAnotherClass, ClassFile.ACC_PUBLIC);

        // Method taking and returning AnotherClass: public AnotherClass processAnother(AnotherClass param) { return param; }
        MethodTypeDesc mtdProcessAnother = java.lang.constant.MethodTypeDesc.of(cdAnotherClass, cdAnotherClass);
        clb.withMethodBody("processAnother", mtdProcessAnother, ClassFile.ACC_PUBLIC, mbc -> mbc
            .aload(1)
            .areturn());

        // Method referencing YetAnotherClass: public void useYetAnother() { new YetAnotherClass(); }
        clb.withMethodBody("useYetAnother", java.lang.constant.ConstantDescs.MTD_void, ClassFile.ACC_PUBLIC, mbc -> mbc
            .new_(cdYetAnotherClass)
            .dup()
            .invokespecial(cdYetAnotherClass, java.lang.constant.ConstantDescs.INIT_NAME, java.lang.constant.ConstantDescs.MTD_void)
            .return_());
    });
  }

  @AfterEach
  void tearDown() throws Throwable {
    if (Files.exists(MAGIK_ROOT_DIR)) {
        try (var walker = Files.walk(MAGIK_ROOT_DIR)) {
            walker.sorted(Comparator.reverseOrder())
                  .forEach(path -> {
                      try {
                          Files.delete(path);
                      } catch (IOException e) {
                          System.err.println("Failed to delete path in tearDown: " + path + " - " + e.getMessage());
                      }
                  });
        } catch (IOException e) {
             System.err.println("Failed to walk path in tearDown: " + MAGIK_ROOT_DIR + " - " + e.getMessage());
        }
    }
  }

  @Test
  void testFindClass() throws Throwable {
    MajestikClassLoader classLoader = new MajestikClassLoader();

    // Test with a valid class name that starts with "magik."
    // This tests the direct loading path in MajestikClassLoader for names
    // starting with "magik" or "majestik", without remapping.
    Class<?> validClass = classLoader.loadClass("magik.T");
    assertNotNull(validClass, "magik.T should be loaded successfully.");
    assertEquals("magik.T", validClass.getName(), "The name of the loaded class should be magik.T.");

    // Ensure the class can be instantiated (basic check of validity)
    validClass.getConstructor().newInstance();
  }

  @Test
  void testRemappingLogic() throws Throwable {
    MajestikClassLoader classLoader = new MajestikClassLoader();

    // Load com.gesmallworld.magik.OriginalTestClass
    Class<?> originalTestClass = classLoader.loadClass("com.gesmallworld.magik.OriginalTestClass");
    assertNotNull(originalTestClass, "Loaded class should not be null");

    // Assert that the loaded class's name is com.keronic.majestik.OriginalTestClass
    assertEquals("com.keronic.majestik.OriginalTestClass", originalTestClass.getName(),
        "Class name should be remapped");

    // Instantiate the loaded class
    Object instance = originalTestClass.getConstructor().newInstance();
    assertNotNull(instance, "Instance should not be null");

    // Verify field type remapping for 'myField'
    // Expected remapped type: com.keronic.majestik.AnotherClass
    Class<?> anotherClassRemapped = classLoader.loadClass("com.gesmallworld.magik.AnotherClass"); // Load it to get the remapped version for comparison
    assertEquals("com.keronic.majestik.AnotherClass", anotherClassRemapped.getName(),
        "AnotherClass name should be remapped for type comparison");

    java.lang.reflect.Field myField = originalTestClass.getDeclaredField("myField");
    assertNotNull(myField, "Field 'myField' should exist");
    assertEquals(anotherClassRemapped, myField.getType(), "Type of 'myField' should be remapped AnotherClass");

    // Verify method parameter and return type remapping for 'processAnother'
    java.lang.reflect.Method processAnotherMethod = originalTestClass.getDeclaredMethod("processAnother", anotherClassRemapped);
    assertNotNull(processAnotherMethod, "Method 'processAnother' should exist with remapped parameter type");
    assertEquals(anotherClassRemapped, processAnotherMethod.getReturnType(),
        "Return type of 'processAnother' should be remapped AnotherClass");

    // Verify remapping of the static string constant field GREETING
    java.lang.reflect.Field greetingField = originalTestClass.getDeclaredField("GREETING");
    assertNotNull(greetingField, "Field 'GREETING' should exist");
    assertEquals(String.class, greetingField.getType(), "Type of 'GREETING' should be String");
    // Make accessible if it's not public (it is public static final, so direct access should be fine)
    // greetingField.setAccessible(true);
    Object greetingValue = greetingField.get(null); // Get value of static field
    assertNotNull(greetingValue, "Value of 'GREETING' should not be null");
    assertEquals("Original package: com.keronic.majestik", greetingValue.toString(),
        "Value of 'GREETING' string constant should be remapped");

    // Verify that useYetAnother method can be invoked (tests remapping of class referenced in method body)
    // Expected remapped type: com.keronic.majestik.YetAnotherClass
    Class<?> yetAnotherClassRemapped = classLoader.loadClass("com.gesmallworld.magik.YetAnotherClass");
    assertEquals("com.keronic.majestik.YetAnotherClass", yetAnotherClassRemapped.getName(),
        "YetAnotherClass name should be remapped for type comparison");

    java.lang.reflect.Method useYetAnotherMethod = originalTestClass.getDeclaredMethod("useYetAnother");
    assertNotNull(useYetAnotherMethod, "Method 'useYetAnother' should exist");
    // Invoking the method will cause a ClassNotFoundException if YetAnotherClass was not remapped correctly during new_ bytecode instruction.
    useYetAnotherMethod.invoke(instance);
  }

  @Test
  void testParentDelegation() throws Throwable {
    MajestikClassLoader classLoader = new MajestikClassLoader();

    // Test loading a standard Java class (e.g., java.util.ArrayList)
    String standardClassName = "java.util.ArrayList";
    Class<?> arrayListClass = classLoader.loadClass(standardClassName);

    assertNotNull(arrayListClass, "Loaded class should not be null");
    assertEquals(standardClassName, arrayListClass.getName(), "Class name should be the same as requested");

    // Verify that the class was loaded by the system classloader (parent) and not MajestikClassLoader
    // MajestikClassLoader's constructor sets getSystemClassLoader() as parent.
    assertEquals(ClassLoader.getSystemClassLoader(), arrayListClass.getClassLoader(),
        "Class should be loaded by the system classloader (parent)");

    // Test loading another standard Java class to be sure
    String stringClassName = "java.lang.String";
    Class<?> stringClass = classLoader.loadClass(stringClassName);
    assertNotNull(stringClass, "String class should not be null");
    assertEquals(stringClassName, stringClass.getName(), "String class name should be correct");
    // Bootstrap classes like java.lang.String might have null classloader, which is fine and indicates the bootstrap loader.
    // The key is that it's not MajestikClassLoader.
    if (stringClass.getClassLoader() != null) { // Primordial classes might have null classloader
        assertNotEquals(classLoader, stringClass.getClassLoader(),
            "java.lang.String should not be loaded by MajestikClassLoader instance");
    } else {
        // This is expected for bootstrap classes, printing info for clarity during test run
        System.out.println("Info: java.lang.String was loaded by the bootstrap class loader (null classloader). This is correct.");
    }

    // Test with a class that is clearly not in 'magik' or 'majestik' packages
    String otherClassName = "org.junit.jupiter.api.Test"; // A class from the test framework itself
    Class<?> junitTestAnnotationClass = classLoader.loadClass(otherClassName);
    assertNotNull(junitTestAnnotationClass, "JUnit Test annotation class should not be null");
    assertEquals(otherClassName, junitTestAnnotationClass.getName(), "JUnit Test annotation class name should be correct");
    assertNotEquals(classLoader, junitTestAnnotationClass.getClassLoader(),
        "JUnit Test annotation class should not be loaded by MajestikClassLoader instance." +
        " Actual loader: " + junitTestAnnotationClass.getClassLoader());
    // It should be loaded by the classloader that loaded MajestikClassLoaderTest itself, typically AppClassLoader or Surefire classloader.
    assertEquals(MajestikClassLoaderTest.class.getClassLoader(), junitTestAnnotationClass.getClassLoader(),
        "JUnit Test annotation class should be loaded by the same loader as the test class itself.");

  }

  @Test
  void testClassNotFoundVariations() throws Throwable {
    MajestikClassLoader classLoader = new MajestikClassLoader();

    // 1. Test loading a class from com.gesmallworld.magik that genuinely does not exist
    String nonExistentMagikClass = "com.gesmallworld.magik.NonExistentTestClass";
    assertThrows(ClassNotFoundException.class, () -> {
      classLoader.loadClass(nonExistentMagikClass);
    }, "Should throw ClassNotFoundException for a non-existent class in remapped package path.");

    // 2. Test loading a class that starts with "magik." but doesn't exist
    // (This is similar to an existing test in testFindClass, but good to have specific variations)
    String nonExistentSimpleMagikClass = "magik.NonExistentSimple";
    assertThrows(ClassNotFoundException.class, () -> {
      classLoader.loadClass(nonExistentSimpleMagikClass);
    }, "Should throw ClassNotFoundException for a non-existent class starting with 'magik.'");

    // 3. Test scenario: Class file in com.gesmallworld.magik path is missing
    //    We expect loadClassData to throw IOException, which findClass catches and wraps in ClassNotFoundException.
    //    To simulate this, we request a class name that would resolve to a path, but we ensure no file is there.
    //    The setUp method creates GW_MAGIK_PACKAGE_DIR. We'll use a name that doesn't map to a created .class file.
    String missingFileMagikClass = "com.gesmallworld.magik.MissingFileTestClass";
    // Sanity check: ensure the file really doesn't exist from setup.
    Path expectedMissingPath = GW_MAGIK_PACKAGE_DIR.resolve("MissingFileTestClass.class");
    if(Files.exists(expectedMissingPath)) {
        // This would be an error in test setup if it exists.
        Files.delete(expectedMissingPath);
    }
    assertThrows(ClassNotFoundException.class, () -> {
      classLoader.loadClass(missingFileMagikClass);
    }, "Should throw ClassNotFoundException if .class file is missing for a class in remapped package.");

    // 4. Test scenario: Class file is malformed (corrupted)
    //    Create a dummy, malformed .class file and try to load it.
    String malformedClassName = "com.gesmallworld.magik.MalformedTestClass";
    Path malformedClassPath = GW_MAGIK_PACKAGE_DIR.resolve("MalformedTestClass.class");
    byte[] malformedBytes = new byte[]{0, 1, 2, 3, 4, 5}; // Clearly not a valid class file
    Files.write(malformedClassPath, malformedBytes);

    assertThrows(ClassNotFoundException.class, () -> { // It might also be a ClassFormatError wrapped, but CNFE is the declared from findClass
      classLoader.loadClass(malformedClassName);
    }, "Should throw ClassNotFoundException (or wrapped error) for a malformed .class file.");

    // Clean up the explicitly created malformed file (tearDown will get the directory later)
    Files.deleteIfExists(malformedClassPath);


    // 5. Test scenario: Class name with valid prefix but invalid/empty package segments
    //    e.g. "com.gesmallworld.magik..MyClass" or "com.gesmallworld.magik."
    //    ClassLoader.loadClass usually handles names like "com.package." by trying to load "com.package"
    //    The behavior for ".." might depend on OS path normalization if it gets that far.
    //    The `loadClass` method in `ClassLoader` itself might throw CNFE before `findClass` is even called for malformed names.
    //    Let's test a name that might make it to our `findClass`.

    String malformedNameInPath = "com.gesmallworld.magik..DoubleDotClass";
     assertThrows(ClassNotFoundException.class, () -> {
      classLoader.loadClass(malformedNameInPath);
    }, "Should throw ClassNotFoundException for a malformed class name with '..'");

    String trailingDotName = "com.gesmallworld.magik.TrailingDot.";
    // Behavior for names ending with "." is that the dot is usually trimmed by ClassLoader.loadClass before calling findClass.
    // So, if "com.gesmallworld.magik.TrailingDot" doesn't exist, it will throw ClassNotFoundException.
    // This will behave like case 1 or 3.
    assertThrows(ClassNotFoundException.class, () -> {
      classLoader.loadClass(trailingDotName);
    }, "Should throw ClassNotFoundException for a name ending with a dot if the trimmed name doesn't exist.");

  }
}

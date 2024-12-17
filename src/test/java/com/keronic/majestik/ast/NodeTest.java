package com.keronic.majestik.ast;

import module java.base;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Base test class providing common utilities and assertions for AST node tests.
 *
 * <p>The {@code NodeTest} class offers shared helper methods and custom assertion utilities that
 * are used across various AST node test cases. By extending this class, individual node test
 * classes can leverage common functionality, ensure consistency, and reduce code duplication in
 * tests.
 *
 * <p>Notably, this class includes methods like {@link #assertNotEqualsNull(Object)}, which provides
 * a standardized way to assert that objects are not equal to {@code null}.
 */
abstract class NodeTest {
  /**
   * Compiles a code builder consumer into a CodeModel for testing.
   *
   * @param cb the code builder consumer to compile
   * @return the resulting CodeModel
   * @throws NullPointerException if cb is null
   * @throws IllegalStateException if method type or code cannot be resolved
   */
  CodeModel compileInto(Consumer<? super CodeBuilder> cb) {
    Objects.requireNonNull(cb, "Code builder consumer cannot be null");

    var mt = MethodType.genericMethodType(0);
    var mtd = mt.describeConstable().orElseThrow();

    var bytes =
        ClassFile.of()
            .build(
                ClassDesc.of(this.getClass().getPackageName() + ".TestClass"),
                clb -> {
                  clb.withMethodBody("testMethod", mtd, ACC_PUBLIC | ACC_STATIC, cb);
                });

    ClassModel cm = ClassFile.of().parse(bytes);
    assertEquals(1, cm.methods().size(), "Generated class should contain exactly one method");
    var code = cm.methods().getFirst().code().orElseThrow();
    assertInstanceOf(CodeModel.class, code, "Generated code should be an instance of CodeModel");
    return code;
  }

  /**
   * Asserts that the provided object is not equal to null.
   *
   * @param actual the object to test
   * @throws AssertionError if actual equals null
   */
  public static void assertNotEqualsNull(Object actual) {
    assertNotEquals(actual, null);
  }
}

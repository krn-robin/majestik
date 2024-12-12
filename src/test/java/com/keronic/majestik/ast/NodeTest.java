package com.keronic.majestik.ast;

import module java.base;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * Provides utility methods for testing AST node compilation into bytecode.
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
}

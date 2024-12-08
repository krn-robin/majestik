package com.keronic.majestik.ast;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;

public class InvocationNode extends Node {
  private final CompoundNode arguments;

  public InvocationNode(CompoundNode arguments) {
    this.arguments = Objects.requireNonNull(arguments);
  }

  /**
   * Compiles this invocation node into bytecode.
   * Each argument is compiled first, followed by an invokedynamic call
   * that sets up a dynamic call site for natural procedure invocation.
   *
   * @param cb The code builder to use for compilation
   */
  @Override
  public void compileInto(CodeBuilder cb) {
    arguments.stream().forEach(a -> a.compileInto(cb));
    cb.invokedynamic(
        DynamicCallSiteDesc.of(
            ConstantDescs.BSM_NATURAL_PROC, "()", ConstantDescs.MTD_ObjectObjectObject));
  }

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case InvocationNode other -> Objects.equals(this.arguments, other.arguments);
      default -> false;
    };
  }

  @Override
  public int hashCode() {
    return arguments.hashCode();
  }

  @Override
  public String toString() {
    return String.format("%s (%s)", super.toString(), arguments);
  }
}

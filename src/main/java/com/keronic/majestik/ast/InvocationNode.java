package com.keronic.majestik.ast;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;

public class InvocationNode extends Node {
  private final CompoundNode arguments;

  public InvocationNode(CompoundNode arguments) {
    this.arguments = Objects.requireNonNull(arguments);
  }

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
      case InvocationNode other -> this.arguments.equals(other.arguments);
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

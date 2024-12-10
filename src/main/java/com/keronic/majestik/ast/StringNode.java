package com.keronic.majestik.ast;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;

/**
 * Represents a string literal node in the AST. This node handles string values using dynamic
 * invocation for string building.
 *
 * @see Node
 */
public class StringNode extends Node {
  private final String value;

  public StringNode(String value) {
    this.value = Objects.requireNonNull(value);
  }

  @Override
  public void compileInto(CodeBuilder cb) {
    cb.invokedynamic(
        DynamicCallSiteDesc.of(
            ConstantDescs.BSM_STRING_BUILDER, "string", ConstantDescs.MTD_Object, this.value));
  }

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case StringNode other -> Objects.equals(this.value, other.value);
      default -> false;
    };
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}

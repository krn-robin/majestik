package com.keronic.majestik.ast;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;

/**
 * Represents a string literal node in the AST. This node handles string values using dynamic
 * invocation for string building.
 *
 * <p>Example:
 *
 * <pre>
 * StringNode node = new StringNode("Hello, World!");
 * // Generates bytecode using invokedynamic for efficient string building
 * </pre>
 *
 * <p>The dynamic invocation mechanism uses BSM_STRING_BUILDER bootstrap method to optimize string
 * handling at runtime.
 *
 * @see Node
 */
public class StringNode extends Node {
  private final String value;

  public StringNode(String value) {
    this.value = Objects.requireNonNull(value);
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

  @Override
  public String toString() {
    return String.format("StringNode{value='%s'}", value);
  }

  @Override
  protected void doCompileInto(final CompilationContext cc) {
    cc.codeBuilder().invokedynamic(
        DynamicCallSiteDesc.of(
            ConstantDescs.BSM_STRING_BUILDER, "string", ConstantDescs.MTD_Object, this.value));
  }
}

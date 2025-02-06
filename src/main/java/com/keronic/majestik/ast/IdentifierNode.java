package com.keronic.majestik.ast;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;

/**
 * Represents an identifier node in the Abstract Syntax Tree. This node type is used to store and
 * process identifier tokens during compilation.
 */
public class IdentifierNode extends Node {
  /** The identifier's string value */
  private final String value;

  public IdentifierNode(String value) {
    this.value = Objects.requireNonNull(value);
  }

  /**
   * Compiles this identifier into bytecode for global variable fetching.
   *
   * @param cb The code builder to emit bytecode into
   */
  @Override
  protected void doCompileInto(final CompilationContext cc) {
    // TODO: Fix hardcoded "sw" package variable
    cc.codeBuilder()
        .invokedynamic(
            DynamicCallSiteDesc.of(
                ConstantDescs.BSM_GLOBAL_FETCHER,
                "fetch",
                ConstantDescs.MTD_Object,
                "sw",
                this.value));
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof IdentifierNode other && Objects.equals(this.value, other.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return String.format("IdentifierNode{value='%s'}", this.value);
  }
}

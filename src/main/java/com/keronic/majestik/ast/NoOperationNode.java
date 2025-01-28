package com.keronic.majestik.ast;

import module java.base;

/**
 * Represents a no-operation node in the abstract syntax tree (AST) of the Majestik language. This
 * node serves as a placeholder or signifies an intentional absence of operation. When compiled, it
 * generates a NOP (no-operation) instruction in the bytecode, effectively doing nothing during
 * execution. This can be useful for maintaining consistent control flow or when an operation is
 * deliberately left empty.
 */
public class NoOperationNode extends Node {
  private static final int NO_OPERATION_HASH = Objects.hashCode("nop");

  public NoOperationNode() {}

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof NoOperationNode;
  }

  @Override
  public int hashCode() {
    return NO_OPERATION_HASH;
  }

  @Override
  public String toString() {
    return "NoOperationNode{}";
  }

  @Override
  protected void doCompileInto(final CompilationContext cc) {
    cc.codeBuilder().nop();
  }
}

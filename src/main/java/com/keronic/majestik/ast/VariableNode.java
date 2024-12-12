package com.keronic.majestik.ast;

import module java.base;

/**
 * AST node representing a variable reference in the Majestik language. Used during compilation to
 * load variable values onto the stack.
 */
public class VariableNode extends Node {
  /** The index of the variable in the local variable table. */
  private final int varIndex;

  /**
   * Creates a new variable node with the specified variable index.
   *
   * @param varIndex the index of the variable in the local variable table
   * @throws IllegalArgumentException if varIndex is negative
   */
  public VariableNode(int varIndex) {
    if (varIndex < 0) throw new IllegalArgumentException("Variable index cannot be negative");

    this.varIndex = varIndex;
  }

  /**
   * Compiles this variable node by generating the appropriate bytecode instruction to store a value
   * from the operand stack into this variable.
   *
   * @param cb the code builder to use for bytecode generation
   * @throws NullPointerException if cb is null
   */
  public void compileIntoSet(CodeBuilder cb) {
    Objects.requireNonNull(cb, "CodeBuilder cannot be null");
    cb.astore(this.varIndex);
  }

  /**
   * Determines whether this VariableNode is equal to another object.
   *
   * @param obj the object to compare with
   * @return true if the other object is a VariableNode with the same varIndex
   */
  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case VariableNode other -> Objects.equals(this.varIndex, other.varIndex);
      default -> false;
    };
  }

  /**
   * Computes the hash code for this VariableNode.
   *
   * @return the hash code based on varIndex
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(this.varIndex);
  }

  /**
   * Returns a string representation of this VariableNode.
   *
   * @return a string in the format VariableNode{idx=varIndex}
   */
  @Override
  public String toString() {
    return String.format("VariableNode{idx=%s}", this.varIndex);
  }

  /**
   * Compiles this variable node by generating the appropriate bytecode instruction to load the
   * variable value onto the operand stack.
   *
   * @param cb the code builder to use for bytecode generation
   * @throws NullPointerException if cb is null
   */
  @Override
  protected void doCompileInto(CodeBuilder cb) {
    Objects.requireNonNull(cb, "CodeBuilder cannot be null");
    cb.aload(this.varIndex);
  }
}

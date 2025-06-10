package com.keronic.majestik.ast;

import com.keronic.majestik.constant.ConstantDescs;

/**
 * AST node representing numeric literals in the source code. Supports both integer (Long) and
 * floating-point (Double) values.
 */
public class NumberNode extends Node {
  /** The numeric value stored in this node. */
  private final Number value;

  public NumberNode(Number value) {
    this.value = value;
  }

  /**
   * Compares this NumberNode to another object for equality.
   *
   * @param obj the object to compare with
   * @return {@code true} if the other object is a NumberNode with the same value; {@code false}
   *     otherwise
   */
  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case NumberNode other -> Objects.equals(this.value, other.value);
      default -> false;
    };
  }

  /**
   * Returns the hash code of this NumberNode based on its value.
   *
   * @return hash code corresponding to the value of this NumberNode
   */
  @Override
  public int hashCode() {
    return value.hashCode();
  }

  /**
   * Returns a string representation of this NumberNode.
   *
   * @return a string in the format {@literal "NumberNode{value=<value>}"}
   */
  @Override
  public String toString() {
    return String.format("NumberNode{value=%s}", value);
  }

  /**
   * Compiles this number node into bytecode.
   *
   * @param cc The compilation context to compile into
   * @throws IllegalStateException if the value type is unsupported
   */
  @Override
  protected void doCompileInto(final CompilationContext cc) {
    final var cb = cc.getCodeBuilder();
    if (this.value instanceof Long n) {
      cb.loadConstant(n);
      cb.invokestatic(ConstantDescs.CD_Long, "valueOf", ConstantDescs.MTD_Longlong);
    } else if (this.value instanceof Double n) {
      cb.loadConstant(n);
      cb.invokestatic(ConstantDescs.CD_Double, "valueOf", ConstantDescs.MTD_Doubledouble);
    } else {
      throw new IllegalStateException("Unsupported number type: " + value.getClass());
    }
  }
}

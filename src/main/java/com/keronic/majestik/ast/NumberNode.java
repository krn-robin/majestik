package com.keronic.majestik.ast;

import module java.base;

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
   * Compiles this number node into bytecode.
   *
   * @param cb The code builder to compile into
   * @throws IllegalStateException if the value type is unsupported
   */
  @Override
  public void compileInto(CodeBuilder cb) {
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

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case NumberNode other -> Objects.equals(this.value, other.value);
      default -> false;
    };
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }
}

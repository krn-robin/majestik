package com.keronic.majestik.ast;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;

public class BooleanNode extends Node {
  private final boolean value;

  /**
   * Creates a new BooleanNode with the specified value.
   *
   * @param value the boolean value to be represented by this node
   */
  public BooleanNode(boolean value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case BooleanNode other -> Objects.equals(this.value, other.value);
      default -> false;
    };
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return String.format("BooleanNode{value=%s}", value);
  }

  @Override
  protected void doCompileInto(CodeBuilder cb) {
    var bool = this.value ? "TRUE" : "FALSE";
    cb.getstatic(ConstantDescs.CD_Boolean, bool, ConstantDescs.CD_Boolean);
  }
}

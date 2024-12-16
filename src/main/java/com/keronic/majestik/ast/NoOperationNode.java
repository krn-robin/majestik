package com.keronic.majestik.ast;

import module java.base;

public class NoOperationNode extends Node {

  public NoOperationNode() {}

  @Override
  public boolean equals(Object obj) {
    return obj instanceof NoOperationNode;
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(null);
  }

  @Override
  public String toString() {
    return String.format("NoOperationNode{}");
  }

  @Override
  protected void doCompileInto(CodeBuilder cb) {
    cb.nop();
  }
}

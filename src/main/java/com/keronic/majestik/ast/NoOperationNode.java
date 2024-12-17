package com.keronic.majestik.ast;

import module java.base;

public class NoOperationNode extends Node {
  private final int HASHCODE = Objects.hashCode("nop");

  public NoOperationNode() {}

  @Override
  public boolean equals(final Object obj) {
    return obj instanceof NoOperationNode;
  }

  @Override
  public int hashCode() {
    return HASHCODE;
  }

  @Override
  public String toString() {
    return String.format("NoOperationNode{}");
  }

  @Override
  protected void doCompileInto(final CodeBuilder cb) {
    cb.nop();
  }
}

package com.keronic.majestik.ast;

import module java.base;

public class VariableNode extends Node {
  private final int varIndex;

  public VariableNode(int varIndex) {
    this.varIndex = varIndex;
  }

  @Override
  public void compileInto(CodeBuilder cb) {
    cb.aload(this.varIndex);
  }
}

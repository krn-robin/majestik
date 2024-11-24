package com.keronic.majestik.ast;

import module java.base;

public class AssignmentNode extends Node {
  private final CompoundNode lhs;
  private final CompoundNode rhs;

  public AssignmentNode(CompoundNode lhs, CompoundNode rhs) {
    this.lhs = lhs;
    this.rhs = rhs;
  }

  @Override
  public void compileInto(CodeBuilder cb) {

  }
}

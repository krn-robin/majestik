package com.keronic.majestik.ast;

import module java.base;

/**
 * Represents an assignment operation in the AST. This node handles the compilation of assignment
 * expressions where a value (rhs) is assigned to a target (lhs).
 */
public class AssignmentNode extends Node {
  /** The left-hand side (target) of the assignment */
  private final CompoundNode lhs;

  /** The right-hand side (value) of the assignment */
  private final CompoundNode rhs;

  public AssignmentNode(CompoundNode lhs, CompoundNode rhs) {
    this.lhs = Objects.requireNonNull(lhs);
    this.rhs = Objects.requireNonNull(rhs);
  }

  @Override
  public void compileInto(CodeBuilder cb) {
    this.rhs.compileInto(cb);
    this.lhs.stream()
        .filter(VariableNode.class::isInstance)
        .map(VariableNode.class::cast)
        .forEach(v -> v.compileIntoSet(cb));
  }
}

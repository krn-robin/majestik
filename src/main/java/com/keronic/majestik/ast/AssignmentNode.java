package com.keronic.majestik.ast;

import module java.base;

/**
 * Represents an assignment operation in the AST. This node handles the compilation of assignment
 * expressions where a value (rhs) is assigned to a target (lhs).
 *
 * @since 0.1
 *     <p>Example:
 *     <pre>
 *     var x = 42;  // AssignmentNode(VariableNode("x"), NumberNode(42))
 *     </pre>
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
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case AssignmentNode other ->
          Objects.equals(this.lhs, other.lhs) && Objects.equals(this.rhs, other.rhs);
      default -> false;
    };
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.lhs, this.rhs);
  }

  @Override
  public String toString() {
    return String.format("AssignmentNode{lhs=%s,rhs=%s}", this.lhs, this.rhs);
  }

  /**
   * Compiles this assignment node into bytecode. First compiles the right-hand side expression,
   * then compiles the assignment to each variable on the left-hand side.
   *
   * @param cb The code builder to use
   */
  @Override
  protected void doCompileInto(CodeBuilder cb) {
    this.rhs.compileInto(cb);
    this.lhs.stream()
        .filter(VariableNode.class::isInstance)
        .map(VariableNode.class::cast)
        .forEach(v -> v.compileIntoSet(cb));
  }
}

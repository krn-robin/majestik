package com.keronic.majestik.ast;

import module java.base;

abstract class BinaryOperatorNode extends Node {
  /** The left-hand side (target) of the operator */
  final Node lhs;

  /** The right-hand side (value) of the operator */
  final Node rhs;

  final String CLASSNAME = this.getClass().getSimpleName();

  BinaryOperatorNode(Node lhs, Node rhs) {
    this.lhs = Objects.requireNonNull(lhs);
    this.rhs = Objects.requireNonNull(rhs);
  }

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case BinaryOperatorNode other ->
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
    return String.format("%s{lhs=%s,rhs=%s}", CLASSNAME, this.lhs, this.rhs);
  }
}

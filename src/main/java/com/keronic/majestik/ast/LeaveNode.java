package com.keronic.majestik.ast;

import module java.base;

public class LeaveNode extends Node {
  private final String name;
  public static final LeaveNode unnamed = new LeaveNode("");

  public LeaveNode(String name) {
    this.name = Objects.requireNonNull(name);
  }

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case LeaveNode other -> Objects.equals(this.name, other.name);
      default -> false;
    };
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.name);
  }

  @Override
  public String toString() {
    return String.format("LeaveNode{value='%s'}", name);
  }

  @Override
  protected void doCompileInto(final CompilationContext cc) {
    var label = this == LeaveNode.unnamed ? cc.lastLabel() : cc.findLabel(this.name);
    cc.codeBuilder().goto_(label.endLabel());
  }
}

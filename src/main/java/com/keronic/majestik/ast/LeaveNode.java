package com.keronic.majestik.ast;

import module java.base;
import java.lang.classfile.CodeBuilder;

public class LeaveNode extends Node {
  private final String name;
  static public final LeaveNode unnamed = new LeaveNode("");

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
    // TODO Auto-generated method stub
    throw new UnsupportedOperationException("Unimplemented method 'doCompileInto'");
  }
}

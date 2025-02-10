package com.keronic.majestik.ast;

import module java.base;

/** Represents a leave expression in the AST. Used for breaking out of labeled blocks or loops. */
public class LeaveNode extends Node {
  /** The label name to leave from, or empty string for unnamed leave. */
  private final String name;

  /** Singleton instance for unnamed leave expressions. */
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
    return String.format("LeaveNode{name='%s'}", name);
  }

  /**
   * Compiles a leave statement into bytecode that jumps to the end of a loop.
   *
   * <p>For an unnamed leave (created with an empty string), it targets the innermost loop by using
   * the last bound label. For a named leave, it targets the loop with the specified label name. The
   * compilation generates a goto instruction that jumps to the end label of the target loop.
   *
   * @param cc The compilation context containing the loop labels and code builder
   * @throws NoSuchElementException if no matching label is found
   */
  @Override
  protected void doCompileInto(final CompilationContext cc) {
    var label = this == LeaveNode.unnamed ? cc.lastLabel() : cc.findLabel(this.name);
    cc.getCodeBuilder().goto_(label.endLabel());
  }
}

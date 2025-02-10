package com.keronic.majestik.ast;

import module java.base;

/**
 * Represents a loop node in the AST, encapsulating a compound statement that can be executed
 * repeatedly.
 */
public class LoopNode extends AbstractCompoundNode {
  private final String name;

  /** Creates a new loop node without children. */
  public LoopNode(String name) {
    super();
    this.name = name;
  }

  /**
   * Creates a new loop node with the given compound node.
   *
   * @param children The compound node containing the loop's statements
   * @throws IllegalArgumentException if children is null
   */
  public LoopNode(String name, CompoundNode children) {
    super(children);
    this.name = name;
  }

  /**
   * Creates a new loop node with the given node.
   *
   * @param child The node containing the loop's statement
   * @throws IllegalArgumentException if child is null
   */
  public LoopNode(String name, Node child) {
    super(child);
    this.name = name;
  }

  /**
   * Checks if this loop node equals another object. Two loop nodes are equal if they have equal
   * names and children.
   *
   * @param obj the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case LoopNode other -> super.equals(other) && Objects.equals(this.name, other.name);
      default -> false;
    };
  }

  /**
   * Returns a hash code value for this loop node.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), this.name);
  }

  /**
   * Compiles this compound node into the given code builder.
   *
   * @param cc The compilation context to compile into
   * @throws NullPointerException if cb is null
   */
  @Override
  protected void doCompileInto(final CompilationContext cc) {
    cc.getCodeBuilder()
        .block(
            bcb -> {
              cc.bindLabel(this.name, bcb.startLabel(), bcb.endLabel());
              this.forEach(node -> node.compileInto(cc.withCodeBuilder(bcb)));
              bcb.goto_(bcb.startLabel());
              cc.popLabel();
            });
  }

  @Override
  public String toString() {
    return String.format("LoopNode{name='%s',children=[%s]}", name, this.childrenString());
  }
}

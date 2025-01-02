package com.keronic.majestik.ast;

import module java.base;

/** Represents a block node in the AST, encapsulating a compound statement. */
public class BlockNode extends Node {
  /** The compound node containing this block's statements. */
  private final CompoundNode children;

  /**
   * Creates a new block node with the given compound node.
   *
   * @param children The compound node containing the block's statements
   * @throws IllegalArgumentException if children is null
   */
  public BlockNode(CompoundNode children) {
    this.children = Objects.requireNonNull(children);
  }

  /**
   * Creates a new block node with the given node.
   *
   * @param child The node containing the block's statement
   * @throws IllegalArgumentException if children is null
   */
  public BlockNode(Node child) {
    var children = new CompoundNode(Objects.requireNonNull(child));
    this(children);
  }

  /**
   * Compiles this block node into the given code builder.
   *
   * @param cb The code builder to compile into
   * @throws IllegalArgumentException if cb is null
   */
  protected void doCompileInto(CodeBuilder cb) {
    children.compileInto(Objects.requireNonNull(cb));
  }

  /**
   * Checks if this block node equals another object. Two block nodes are equal if they have equal
   * children.
   *
   * @param obj the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case BlockNode other -> Objects.equals(this.children, other.children);
      default -> false;
    };
  }

  /**
   * Returns a hash code value for this block node.
   *
   * @return a hash code value for this object
   */
  @Override
  public int hashCode() {
    return Objects.hashCode(this.children);
  }

  /**
   * Returns a string representation of this block node.
   *
   * @return a string representation of this object
   */
  @Override
  public String toString() {
    return String.format("BlockNode{children=%s}", this.children);
  }
}

package com.keronic.majestik.ast;

/** Represents a block node in the AST, encapsulating a compound statement. */
public class BlockNode extends AbstractCompoundNode {
  /** Creates a new block node without children. */
  public BlockNode() {
    super();
  }

  /**
   * Creates a new block node with the given compound node.
   *
   * @param children The compound node containing the block's statements
   * @throws IllegalArgumentException if children is null
   */
  public BlockNode(CompoundNode children) {
    super(children);
  }

  /**
   * Creates a new block node with the given node.
   *
   * @param child The node containing the block's statement
   * @throws IllegalArgumentException if child is null
   */
  public BlockNode(Node child) {
    super(child);
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
      case BlockNode other -> super.equals(other);
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
    return super.hashCode();
  }
}

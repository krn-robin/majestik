package com.keronic.majestik.ast;

/** Represents a block node in the AST, encapsulating a compound statement. */
public class LoopNode extends AbstractCompoundNode {
    private final String name;

  /** Creates a new block node without children. */
  public LoopNode(String name) {
    super();
    this.name = name;
  }

  /**
   * Creates a new block node with the given compound node.
   *
   * @param children The compound node containing the block's statements
   * @throws IllegalArgumentException if children is null
   */
  public LoopNode(String name, CompoundNode children) {
    super(children);
    this.name = name;
  }

  /**
   * Creates a new block node with the given node.
   *
   * @param child The node containing the block's statement
   * @throws IllegalArgumentException if child is null
   */
  public LoopNode(String name, Node child) {
    super(child);
    this.name = name;
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

  /**
   * Compiles this compound node into the given code builder.
   *
   * @param cb The code builder to compile into
   * @throws NullPointerException if cb is null
   */
  protected void doCompileInto(final CompilationContext cc) {
    cc.codeBuilder().block(bcb -> {
      cc.bindLabel(this.name, bcb.startLabel(), bcb.endLabel());
      this.forEach(node -> node.compileInto(cc.withCodeBuilder(bcb)));
      bcb.pop();
      bcb.goto_(bcb.startLabel());
      cc.popLabel();
    });
  }
}

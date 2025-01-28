package com.keronic.majestik.ast;

import module java.base;

/**
 * Represents a compound node in the Abstract Syntax Tree that can contain multiple child nodes.
 * This node type is used to group related nodes together, such as statements in a block or elements
 * in a list.
 *
 * @see Node
 */
abstract public class AbstractCompoundNode extends Node {
  private final String CLASSNAME = this.getClass().getSimpleName();

  /** The nodes containing this block's statements. */
  private final Node[] children;

  static final Node EMPTYCOMPOUNDNODE = new ExpressionListNode();

  public AbstractCompoundNode() {
    this.children = new Node[0];
  }

  public AbstractCompoundNode(Node first) {
    this.children = new Node[] {Objects.requireNonNull(first)};
  }

  public AbstractCompoundNode(Node... nodes) {
    Arrays.stream(nodes).forEach(Objects::requireNonNull);
    this.children = Arrays.copyOf(nodes, nodes.length);
  }

  public AbstractCompoundNode(AbstractCompoundNode comp, Node addition) {
    Objects.requireNonNull(comp);
    this.children = Arrays.copyOf(comp.children, comp.children.length + 1);
    this.children[comp.children.length] = Objects.requireNonNull(addition);
  }

  /**
   * Returns a string representation of this node.
   *
   * @return a string representation of this object
   */
  @Override
  public String toString() {
    return String.format("%s{children=[%s]}", CLASSNAME, this.childrenString());
  }

  public int getChildCount() {
    return children.length;
  }

  public Node getChild(int index) {
    return children[index];
  }

  /**
   * Returns a stream of child nodes. Note: If the Node class is mutable, modifications to the nodes
   * in the stream will affect this CompoundNode's state.
   *
   * @return a stream of child nodes
   */
  public Stream<Node> stream() {
    return Arrays.stream(children);
  }

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case AbstractCompoundNode other -> Arrays.equals(this.children, other.children);
      default -> false;
    };
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(children);
  }

  public void forEach(Consumer<? super Node> action) {
    this.stream().forEach(action);
  }

  public boolean isEmpty() {
    return this.children.length == 0;
  }

  protected String childrenString() {
    var joiner = new StringJoiner(",");
    this.stream().map(node -> node.toString()).forEach(joiner::add);
    return joiner.toString();
  }

  /**
   * Compiles this compound node into the given code builder.
   *
   * @param cb The code builder to compile into
   * @throws NullPointerException if cb is null
   */
  protected void doCompileInto(final CompilationContext cc) {
    this.forEach(node -> node.compileInto(cc));
  }
}

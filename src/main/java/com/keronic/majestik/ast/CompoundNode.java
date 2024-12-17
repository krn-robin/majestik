package com.keronic.majestik.ast;

import module java.base;

/**
 * Represents a compound node in the Abstract Syntax Tree that can contain multiple child nodes.
 * This node type is used to group related nodes together, such as statements in a block or elements
 * in a list.
 *
 * @see Node
 */
public class CompoundNode extends Node {
  private final Node[] children;

  public CompoundNode() {
    this.children = new Node[0];
  }

  public CompoundNode(Node first) {
    this.children = new Node[] {Objects.requireNonNull(first)};
  }

  public CompoundNode(Node... nodes) {
    Arrays.stream(nodes).forEach(Objects::requireNonNull);
    this.children = Arrays.copyOf(nodes, nodes.length);
  }

  public CompoundNode(CompoundNode comp, Node addition) {
    Objects.requireNonNull(comp);
    this.children = Arrays.copyOf(comp.children, comp.children.length + 1);
    this.children[comp.children.length] = Objects.requireNonNull(addition);
  }

  @Override
  public String toString() {
    var joiner = new StringJoiner(",");
    this.stream().map(node -> node.toString()).forEach(joiner::add);
    return String.format("CompoundNode{children=[%s]}", joiner.toString());
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
      case CompoundNode other -> Arrays.equals(this.children, other.children);
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

  protected void doCompileInto(CodeBuilder cb) {
    this.forEach(node -> node.compileInto(cb));
  }
}

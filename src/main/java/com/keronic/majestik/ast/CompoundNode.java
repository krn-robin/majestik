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
    this.children = Arrays.copyOf(nodes, nodes.length);
  }

  public CompoundNode(CompoundNode comp, Node addition) {
    this.children = Arrays.copyOf(comp.children, comp.children.length + 1);
    this.children[comp.children.length] = Objects.requireNonNull(addition);
    ;
  }

  public void compileInto(CodeBuilder cb) {
    Arrays.stream(children).forEach(node -> node.compileInto(cb));
  }

  @Override
  public String toString() {
    var joiner = new StringJoiner("", " (", ")");
    Arrays.stream(children).map(node -> "(" + node.toString() + ")").forEach(joiner::add);
    return super.toString() + joiner.toString();
  }

  public int getChildCount() {
    return children.length;
  }

  public Node getChild(int index) {
    return children[index];
  }

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
}

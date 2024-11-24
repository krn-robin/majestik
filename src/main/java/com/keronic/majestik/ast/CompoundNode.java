package com.keronic.majestik.ast;

import module java.base;

public class CompoundNode extends Node {
  private final Node[] children;

  public CompoundNode() {
    this.children = new Node[0];
  }

  public CompoundNode(Node first) {
    this.children = new Node[] {first};
  }

  public CompoundNode(CompoundNode comp, Node addition) {
    this.children = Arrays.copyOf(comp.children, comp.children.length + 1);
    this.children[comp.children.length] = addition;
  }

  public void compileInto(CodeBuilder cb) {
    Arrays.stream(children).forEach(node -> node.compileInto(cb));
  }

  @Override
  public String toString() {
    var sb = new StringBuilder(super.toString());
    sb.append(" (");
    Arrays.stream(children)
        .forEach(
            node -> {
              sb.append("(");
              sb.append(node.toString());
              sb.append(")");
            });
    return sb.toString();
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
      case CompoundNode other -> Arrays.equals(this.children, other.children);
      default -> false;
    };
  }

}

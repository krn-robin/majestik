package com.keronic.majestik.ast;

public class CompoundNode extends AbstractCompoundNode {

  public CompoundNode(Node... nodes) {
    super(nodes);
  }

  public CompoundNode(CompoundNode comp, Node addition) {
    super(comp, addition);
  }
}

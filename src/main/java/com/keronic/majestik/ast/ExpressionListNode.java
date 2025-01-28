package com.keronic.majestik.ast;

public class ExpressionListNode extends AbstractCompoundNode {

  public ExpressionListNode(ExpressionListNode c, Node second) {
    super(c, second);
  }

  public ExpressionListNode(Node... nodes) {
    super(nodes);
  }
}

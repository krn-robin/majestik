package com.keronic.majestik.ast;

/**
 * Represents a list of expressions in the AST. This node type is used to group multiple expressions
 * together.
 */
public class ExpressionListNode extends AbstractCompoundNode {

  /**
   * Creates an ExpressionListNode by appending a node to an existing list.
   *
   * @param c The existing expression list
   * @param second The node to append
   */
  public ExpressionListNode(ExpressionListNode c, Node second) {
    super(c, second);
  }

  /**
   * Creates an ExpressionListNode from a variable number of nodes.
   *
   * @param nodes The nodes to include in the list
   */
  public ExpressionListNode(Node... nodes) {
    super(nodes);
  }
}

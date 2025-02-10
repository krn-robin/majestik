package com.keronic.majestik.ast;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;

/** IfExpressionNode */
public class IfExpressionNode extends Node {
  private final Node condition;
  private final Node body;
  private final Node elseBody;

  static final Node EMPTYCOMPOUNDNODE = new ExpressionListNode();

  public IfExpressionNode(Node condition, Node body, Node elseBody) {
    this.condition = Objects.requireNonNull(condition);
    this.body = body != null ? body : EMPTYCOMPOUNDNODE;
    this.elseBody = elseBody != null ? elseBody : EMPTYCOMPOUNDNODE;
  }

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case IfExpressionNode other ->
          Objects.equals(this.condition, other.condition)
              && Objects.equals(this.body, other.body)
              && Objects.equals(this.elseBody, other.elseBody);
      default -> false;
    };
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.condition, this.body, this.elseBody);
  }

  @Override
  public String toString() {
    return String.format(
        "IfExpressionNode{condition=%s,body=%s,else=%s}", this.condition, this.body, this.elseBody);
  }

  @Override
  protected void doCompileInto(final CompilationContext cc) {
    final var cb = cc.getCodeBuilder();
    this.condition.compileInto(cc);
    cb.invokestatic(
        ConstantDescs.CD_MagikObjectUtils, "should_be_boolean", ConstantDescs.MTD_booleanObject);
    cb.ifThenElse(
        bcb -> this.body.compileInto(cc.withCodeBuilder(bcb)),
        bcb -> this.elseBody.compileInto(cc.withCodeBuilder(bcb)));
  }
}

package com.keronic.majestik.ast;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;

/** IfExpressionNode */
public class IfExpressionNode extends Node {
  private final Node condition;
  private final Node body;
  private final Node elseBody;

  public IfExpressionNode(Node condition, Node body, Node elseBody) {
    this.condition = Objects.requireNonNull(condition);
    this.body = body != null ? body : new CompoundNode();
    this.elseBody = elseBody != null ? elseBody : new CompoundNode();
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
  protected void doCompileInto(CodeBuilder cb) {
    this.condition.compileInto(cb);
    cb.invokestatic(
        ConstantDescs.CD_MagikObjectUtils, "should_be_boolean", ConstantDescs.MTD_booleanObject);
    cb.ifThenElse(bcb -> this.body.compileInto(bcb), bcb -> this.elseBody.compileInto(bcb));
  }
}

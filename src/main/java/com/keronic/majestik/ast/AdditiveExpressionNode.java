package com.keronic.majestik.ast;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;

public class AdditiveExpressionNode extends BinaryOperatorNode {
  public AdditiveExpressionNode(Node lhs, Node rhs) {
    super(lhs, rhs);
  }

  @Override
  protected void doCompileInto(final CompilationContext cc) {
    lhs.compileInto(cc);
    rhs.compileInto(cc);
    cc.getCodeBuilder()
        .invokedynamic(
            DynamicCallSiteDesc.of(
                ConstantDescs.BSM_BINARY_DISPATCHER, "+", ConstantDescs.MTD_ObjectObjectObject));
  }
}

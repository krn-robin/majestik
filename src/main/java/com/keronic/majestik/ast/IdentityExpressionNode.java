package com.keronic.majestik.ast;

import com.keronic.majestik.constant.ConstantDescs;

class IdentityExpressionNode extends BinaryOperatorNode {
  IdentityExpressionNode(final Node lhs, final Node rhs) {
    super(lhs, rhs);
  }

  @Override
  protected void doCompileInto(final CompilationContext cc) {
    var cb = cc.codeBuilder();
    lhs.compileInto(cc);
    rhs.compileInto(cc);
    cb.invokestatic(ConstantDescs.CD_MagikObjectUtils, "is", ConstantDescs.MTD_booleanObjectObject);
    cb.new_(ConstantDescs.CD_Boolean);
    cb.dup_x1();
    cb.swap();
    cb.invokespecial(
        ConstantDescs.CD_Boolean, ConstantDescs.INIT_NAME, ConstantDescs.MTD_voidboolean);
  }
}

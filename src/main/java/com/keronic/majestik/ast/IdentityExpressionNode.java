package com.keronic.majestik.ast;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;

class IdentityExpressionNode extends BinaryOperatorNode {
  IdentityExpressionNode(final Node lhs, final Node rhs) {
    super(lhs, rhs);
  }

  @Override
  public void doCompileInto(CodeBuilder cb) {
    lhs.compileInto(cb);
    rhs.compileInto(cb);
    cb.invokestatic(ConstantDescs.CD_MagikObjectUtils, "is", ConstantDescs.MTD_booleanObjectObject);
    cb.new_(ConstantDescs.CD_Boolean);
    cb.dup_x1();
    cb.swap();
    cb.invokespecial(
        ConstantDescs.CD_Boolean, ConstantDescs.INIT_NAME, ConstantDescs.MTD_voidboolean);
  }
}

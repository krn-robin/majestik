package com.keronic.majestik.ast;

import module java.base;
import com.keronic.majestik.constant.ConstantDescs;

public class NumberNode extends Node {
  private final Number value;

  public NumberNode(Number value) {
    this.value = value;
  }

  @Override
  public void compileInto(CodeBuilder cb) {
    if (this.value instanceof Long n) {
      cb.loadConstant(n);
      cb.invokestatic(ConstantDescs.CD_Long, "valueOf", ConstantDescs.MTD_Longlong);
    } else if (this.value instanceof Double n) {
      cb.loadConstant(n);
      cb.invokestatic(ConstantDescs.CD_Double, "valueOf", ConstantDescs.MTD_Doubledouble);
    }
  }
}

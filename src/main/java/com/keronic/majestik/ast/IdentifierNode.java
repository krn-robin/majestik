package com.keronic.majestik.ast;

import module java.base;
import com.keronic.majestik.constant.ConstantDescs;

public class IdentifierNode extends Node {
  private final String value;

  public IdentifierNode(String value) {
    this.value = value;
  }

  @Override
  public void compileInto(CodeBuilder cb) {
    cb.invokedynamic( DynamicCallSiteDesc.of(
      ConstantDescs.BSM_GLOBAL_FETCHER, "fetch", ConstantDescs.MTD_Object, "sw", this.value));
  }

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case IdentifierNode other -> this.value.equals(other.value);
      default -> false;
    };
  }

}

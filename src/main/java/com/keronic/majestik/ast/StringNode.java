package com.keronic.majestik.ast;

import module java.base;
import com.keronic.majestik.constant.ConstantDescs;

public class StringNode extends Node {
  private final String value;

  public StringNode(String value) {
    this.value = value;
  }

  @Override
  public void compileInto(CodeBuilder cb) {
    cb.invokedynamic(DynamicCallSiteDesc.of(ConstantDescs.BSM_STRING_BUILDER, "string",
        ConstantDescs.MTD_Object, this.value));
  }

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case StringNode other -> this.value.equals(other.value);
      default -> false;
    };
  }
}

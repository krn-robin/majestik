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
    cb.invokedynamic(DynamicCallSiteDesc.of(ConstantDescs.BSM_STRING_BUILDER, "string",
	ConstantDescs.MTD_Object, this.value));
  }
}

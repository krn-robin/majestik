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
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    StringNode other = (StringNode) obj;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }
}

package com.keronic.majestik.ast;

import module java.base;
import com.keronic.majestik.constant.ConstantDescs;

public class InvocationNode extends Node {
  final CompoundNode arguments;

  public InvocationNode(CompoundNode arguments) {
    this.arguments = arguments;
  }

  @Override
  public void compileInto(CodeBuilder cb) {
    arguments.stream().forEach(a -> a.compileInto(cb));
    // https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/invoke/CallSite.html
    cb.invokedynamic(DynamicCallSiteDesc.of(ConstantDescs.BSM_NATURAL_PROC, "()",
        ConstantDescs.MTD_ObjectObjectObject));

  }

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case InvocationNode other -> this.arguments.equals(other.arguments);
      default -> false;
    };
  }

  @Override
  public String toString() {
    var sb = new StringBuilder(super.toString());
    sb.append(" (");
    sb.append(this.arguments);
    sb.append(" )");
    return sb.toString();
  }
}

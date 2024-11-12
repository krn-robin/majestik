package com.keronic.majestik.ast;

import module java.base;

public class BlockNode extends CompoundNode {
  private final CompoundNode children;

  public BlockNode(CompoundNode children) {
    this.children = children;
  }

  public void compileInto(CodeBuilder cb) {
    children.compileInto(cb);
  }
}

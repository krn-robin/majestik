package com.keronic.majestik.ast;

import module java.base;

public abstract class Node {
  public abstract void compileInto(CodeBuilder cb);
}

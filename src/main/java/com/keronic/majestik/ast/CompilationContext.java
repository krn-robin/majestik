package com.keronic.majestik.ast;

import module java.base;

public class CompilationContext {
  record BoundLabel(String name, Label startLabel, Label endLabel) { }
  final Deque<BoundLabel> loopStack;
  final CodeBuilder codeBuilder;

  public CompilationContext(CodeBuilder codeBuilder) {
    this(new ArrayDeque<BoundLabel>(), codeBuilder);
  }

  private CompilationContext(Deque<BoundLabel> loopStack, CodeBuilder codeBuilder) {
    this.codeBuilder = codeBuilder;
    this.loopStack = loopStack;
  }

  public CodeBuilder codeBuilder() {
    return this.codeBuilder;
  }

  public CompilationContext withCodeBuilder(CodeBuilder codeBuilder) {
    return new CompilationContext(this.loopStack, codeBuilder);
  }
}

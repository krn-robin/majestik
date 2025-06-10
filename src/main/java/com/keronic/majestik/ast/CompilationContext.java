package com.keronic.majestik.ast;

import java.util.NoSuchElementException;

public class CompilationContext {
  record BoundLabel(String name, Label startLabel, Label endLabel) {}

  final Deque<BoundLabel> loopStack;
  final CodeBuilder codeBuilder;

  public CompilationContext(CodeBuilder codeBuilder) {
    this(new ArrayDeque<BoundLabel>(), codeBuilder);
  }

  private CompilationContext(Deque<BoundLabel> loopStack, CodeBuilder codeBuilder) {
    this.codeBuilder = codeBuilder;
    this.loopStack = loopStack;
  }

  public CodeBuilder getCodeBuilder() {
    return this.codeBuilder;
  }

  public CompilationContext withCodeBuilder(CodeBuilder codeBuilder) {
    return new CompilationContext(this.loopStack, codeBuilder);
  }

  public BoundLabel bindLabel(String name, Label startLabel, Label endLabel) {
    var label = new BoundLabel(name, startLabel, endLabel);
    this.loopStack.addLast(label);
    return label;
  }

  public BoundLabel findLabel(final String name) {
    var iter = this.loopStack.descendingIterator();

    while (iter.hasNext()) {
      var boundLabel = iter.next();
      if (name.equals(boundLabel.name)) return boundLabel;
    }
    throw new NoSuchElementException("Label not found: " + name);
  }

  public BoundLabel lastLabel() {
    return this.loopStack.getLast();
  }

  public void popLabel() {
    this.loopStack.removeLast();
  }
}

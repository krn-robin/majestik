package com.keronic.majestik.ast;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;

/**
 * Represents a method or function invocation node in the Majestik abstract syntax tree (AST).
 *
 * <p>An invocation consists of a sequence of arguments that are passed to a method or function. For
 * example, in the expression `write("Hello")`, the arguments would be a StringNode containing
 * "Hello". The target method/function is determined by the preceding node in the AST.
 *
 * <p>Example usage:
 *
 * <pre>
 * // For code: write("Hello", "World")
 * CompoundNode args = new CompoundNode(
 *     new StringNode("Hello"),
 *     new StringNode("World")
 * );
 * InvocationNode invoke = new InvocationNode(args);
 * </pre>
 *
 * @see Node
 * @see CompoundNode
 * @see StringNode
 */
public class InvocationNode extends Node {
  private final CompoundNode arguments;

  public InvocationNode(CompoundNode arguments) {
    this.arguments = Objects.requireNonNull(arguments);
  }

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case InvocationNode other -> Objects.equals(this.arguments, other.arguments);
      default -> false;
    };
  }

  @Override
  public int hashCode() {
    return arguments.hashCode();
  }

  @Override
  public String toString() {
    return String.format("InvocationNode{arguments=%s}", arguments);
  }

  /**
   * Compiles this invocation node into bytecode. Each argument is compiled first, followed by an
   * invokedynamic call that sets up a dynamic call site for natural procedure invocation.
   *
   * @param cb The code builder to use for compilation
   */
  @Override
  protected void doCompileInto(CodeBuilder cb) {
    this.arguments.forEach(a -> a.compileInto(cb));
    cb.invokedynamic(
        DynamicCallSiteDesc.of(
            ConstantDescs.BSM_NATURAL_PROC, "()", ConstantDescs.MTD_ObjectObjectObject));
  }
}

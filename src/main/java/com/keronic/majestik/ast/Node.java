package com.keronic.majestik.ast;

import module java.base;

/**
 * Base class for all Abstract Syntax Tree (AST) nodes in the Majestik language. This class is part
 * of the Magik-squid based implementation, replacing the previous ANTLR4 based parser.
 *
 * <p>Each concrete subclass represents a specific language construct (e.g., expressions,
 * statements, declarations) and must implement: - compileInto: for bytecode generation -
 * equals/hashCode: for node comparison and structural equality - toString: for debugging and error
 * reporting
 */
public abstract class Node {
  /**
   * Compiles this AST node into bytecode using the provided CodeBuilder.
   *
   * @param cb The CompilationContext instance to use for bytecode generation
   * @throws NullPointerException if cb is null
   */
  public void compileInto(CompilationContext cc) {
    Objects.requireNonNull(cc);
    doCompileInto(cc);
  }

  @Override
  /**
   * Compares this node with another object for structural equality. Two nodes are equal if they are
   * of the same type and have equal children.
   *
   * @param obj The object to compare with
   * @return true if the objects are structurally equal
   */
  public abstract boolean equals(Object obj);

  @Override
  /**
   * Returns a hash code consistent with equals(). Implementations should include all fields used in
   * equals().
   *
   * @return The hash code value
   */
  public abstract int hashCode();

  @Override
  /**
   * Returns a string representation of this node for debugging. The format should include the node
   * type and essential attributes.
   *
   * @return A debug-friendly string representation
   */
  public abstract String toString();

  /**
   * Template method for actual bytecode generation. Subclasses implement this method instead of
   * compileInto directly.
   *
   * @param cb The non-null CompilationContext instance
   */
  protected abstract void doCompileInto(CompilationContext cc);
}

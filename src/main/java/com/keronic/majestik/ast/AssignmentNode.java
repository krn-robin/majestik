package com.keronic.majestik.ast;

/**
 * Represents an assignment operation in the AST. This node handles the compilation of assignment
 * expressions where a value (rhs) is assigned to a target (lhs).
 *
 * @since 0.1
 *     <p>Example:
 *     <pre>
 *     var x = 42;  // AssignmentNode(VariableNode("x"), NumberNode(42))
 *     </pre>
 */
public class AssignmentNode extends BinaryOperatorNode {

  public AssignmentNode(VariableNode lhs, Node rhs) {
    super(lhs, rhs);
  }

  /**
   * Compiles this assignment node into bytecode. First compiles the right-hand side expression,
   * then compiles the assignment to each variable on the left-hand side.
   *
   * @param cc The compilation context to compile into
   */
  @Override
  protected void doCompileInto(final CompilationContext cc) {
    this.rhs.compileInto(cc);
    var vnode = (VariableNode) this.lhs;
    vnode.compileIntoSet(cc);
  }
}

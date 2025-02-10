package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nl.ramsolutions.sw.magik.MagikFile;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link MajestikCodeVisitor} which verifies the visitor's behavior in processing
 * AST nodes.
 */
class MajestikCodeVisitorTest {
  @Test
  void testVisitBoolean() {
    // Test input
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("_true%n" + "_false%n").formatted());

    // Execute
    var node = mcv.scanFile(mf);

    // Verify
    assertNotNull(node, "Result should not be null");
    assertInstanceOf(ExpressionListNode.class, node, "Expected ExpressionListNode");
    var cnode = (ExpressionListNode) node;
    assertEquals(2, cnode.getChildCount(), "Expected two string nodes");
    var child0 = cnode.getChild(0);
    var child1 = cnode.getChild(1);
    assertInstanceOf(BooleanNode.class, child0, "First child should be StringNode");
    assertInstanceOf(BooleanNode.class, child1, "Second child should be StringNode");
    assertEquals(new BooleanNode(true), child0);
    assertEquals(new BooleanNode(false), child1);
  }

  @Test
  void testVisitAdditiveExpression() {
    // Test input
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("1 + 1%n").formatted());

    // Execute
    var node = mcv.scanFile(mf);

    // Verify
    assertNotNull(node, "Result should not be null");
    assertInstanceOf(ExpressionListNode.class, node, "Expected ExpressionListNode");
    var cnode = (ExpressionListNode) node;
    assertEquals(1, cnode.getChildCount(), "Expected one addtive node");
    var child0 = cnode.getChild(0);
    assertInstanceOf(
        AdditiveExpressionNode.class, child0, "First child should be AdditiveExpressionNode");
  }

  @Test
  void testVisitAssignmentExpression() {
    // Test input
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("a << 1%n").formatted());

    // Execute
    var node = mcv.scanFile(mf);

    // Verify
    assertNotNull(node, "Result should not be null");
    assertInstanceOf(ExpressionListNode.class, node, "Expected ExpressionListNode");
    var cnode = (ExpressionListNode) node;
    assertEquals(1, cnode.getChildCount(), "Expected one assignment node");
    var child0 = cnode.getChild(0);
    assertInstanceOf(AssignmentNode.class, child0, "First child should be AssignmentNode");
  }

  @Test
  void testVisitBlock() {
    // Test input
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("_block%n_endblock%n").formatted());

    // Execute
    var node = mcv.scanFile(mf);

    // Verify
    assertNotNull(node, "Result should not be null");
    assertInstanceOf(ExpressionListNode.class, node, "Expected ExpressionListNode");
    var cnode = (ExpressionListNode) node;

    assertEquals(1, cnode.getChildCount(), "Expected one block node");
    var child0 = cnode.getChild(0);
    assertInstanceOf(BlockNode.class, child0, "First child should be BlockNode");
    assertEquals(new BlockNode(), child0);
  }

  @Test
  void testVisitCharacter() {
    // Test input
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("%%a%n").formatted());

    // Execute
    var node = mcv.scanFile(mf);

    // Verify
    assertNotNull(node, "Result should not be null");
    assertInstanceOf(ExpressionListNode.class, node, "Expected ExpressionListNode");
    var cnode = (ExpressionListNode) node;

    assertEquals(1, cnode.getChildCount(), "Expected one character node");
    var child0 = cnode.getChild(0);
    assertInstanceOf(CharacterNode.class, child0, "First child should be CharacterNode");
    assertEquals(new CharacterNode('a'), child0);
  }

  @Test
  void testVisitEqualityExpression() {
    // Test input
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("1 _is _true%n").formatted());

    // Execute
    var node = mcv.scanFile(mf);

    // Verify
    assertNotNull(node, "Result should not be null");
    assertInstanceOf(ExpressionListNode.class, node, "Expected ExpressionListNode");
    var cnode = (ExpressionListNode) node;

    assertEquals(1, cnode.getChildCount(), "Expected one equality expression node");
    var child0 = cnode.getChild(0);
    assertInstanceOf(
        IdentityExpressionNode.class, child0, "First child should be IdentityExpressionNode");
  }

  @Test
  void testVisitIfExpression() {
    // Test input
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("_if _true _then _else _endif%n").formatted());

    // Execute
    var node = mcv.scanFile(mf);

    // Verify
    assertNotNull(node, "Result should not be null");
    assertInstanceOf(ExpressionListNode.class, node, "Expected ExpressionListNode");
    var cnode = (ExpressionListNode) node;
    assertEquals(1, cnode.getChildCount(), "Expected one branch node");
    var child0 = cnode.getChild(0);
    assertInstanceOf(IfExpressionNode.class, child0, "First child should be IfExpressionNode");
  }

  @Test
  void testVisitInvoke() {
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("write(0)%n").formatted());

    // Execute
    var node = mcv.scanFile(mf);
    assertNotNull(node, "Result should not be null");
    assertInstanceOf(ExpressionListNode.class, node, "Expected ExpressionListNode");
    var n = (ExpressionListNode) node;

    // Verify
    assertEquals(1, n.getChildCount(), "Expected one child node");
    n = (ExpressionListNode) n.getChild(0);
    assertEquals(new IdentifierNode("write"), n.getChild(0), "Expected write identifier");

    var expectedInvocation = new InvocationNode(new CompoundNode(new NumberNode(0)));
    var actualInvocation = n.getChild(1);
    assertInstanceOf(InvocationNode.class, actualInvocation, "Expected InvocationNode");

    assertEquals(
        expectedInvocation.hashCode(),
        actualInvocation.hashCode(),
        "Expected matching invocation nodes");
  }

  @Test
  void testVisitLoopWithLeave() {
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("_loop @named%n_leave%n_endloop%n").formatted());

    // Execute
    var node = mcv.scanFile(mf);
    assertNotNull(node, "Result should not be null");
    assertInstanceOf(ExpressionListNode.class, node, "Expected ExpressionListNode");
    var n = (ExpressionListNode) node;

    // Verify
    assertEquals(1, n.getChildCount(), "Expected one child node");

    var expectedLoopNode = new LoopNode("named", new LeaveNode(""));
    var actualLoopNode = n.getChild(0);

    assertEquals(expectedLoopNode, actualLoopNode, "Expected matching loop nodes");
    // Optionally, also verify the structure explicitly
    assertInstanceOf(LoopNode.class, actualLoopNode, "Expected LoopNode");
    var loopNode = (LoopNode) actualLoopNode;
    assertInstanceOf(LeaveNode.class, loopNode.getChild(0), "Expected LeaveNode in body");
  }

  @Test
  void testVisitString() {
    // Test input
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("\"string1\"%n" + "'string2'%n").formatted());

    // Execute
    var node = mcv.scanFile(mf);

    // Verify
    assertNotNull(node, "Result should not be null");
    assertInstanceOf(ExpressionListNode.class, node, "Expected ExpressionListNode");
    var cnode = (ExpressionListNode) node;
    assertEquals(2, cnode.getChildCount(), "Expected two string nodes");
    var child0 = cnode.getChild(0);
    var child1 = cnode.getChild(1);
    assertInstanceOf(StringNode.class, child0, "First child should be StringNode");
    assertInstanceOf(StringNode.class, child1, "Second child should be StringNode");
    assertEquals(new StringNode("string1"), child0);
    assertEquals(new StringNode("string2"), child1);
  }
}

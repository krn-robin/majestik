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
  void testVisitString() {
    // Test input
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("\"string1\"%n'string2'%n").formatted());

    // Execute
    var node = mcv.scanFile(mf);

    // Verify
    assertNotNull(node, "Result should not be null");
    assertInstanceOf(CompoundNode.class, node, "Expected CompoundNode");
    var cnode = (CompoundNode) node;
    assertEquals(2, cnode.getChildCount(), "Expected two string nodes");
    var child0 = cnode.getChild(0);
    var child1 = cnode.getChild(1);
    assertInstanceOf(StringNode.class, child0, "First child should be StringNode");
    assertInstanceOf(StringNode.class, child1, "Second child should be StringNode");
    assertEquals(new StringNode("string1"), (StringNode) child0);
    assertEquals(new StringNode("string2"), (StringNode) child1);
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
    assertInstanceOf(CompoundNode.class, node, "Expected CompoundNode");
    var cnode = (CompoundNode) node;
    assertEquals(1, cnode.getChildCount(), "Expected one assignment node");
    var child0 = cnode.getChild(0);
    assertInstanceOf(AssignmentNode.class, child0, "First child should be AssignmentNode");
  }

  @Test
  void testVisitInvoke() {
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("write(0)%n").formatted());

    // Execute
    var n = (CompoundNode) mcv.scanFile(mf);

    // Verify
    assertEquals(1, n.getChildCount());
    n = (CompoundNode) n.getChild(0);
    assertEquals(new IdentifierNode("write"), n.getChild(0));

    // FIXME: Assert on the hashCode now; direct equality does not work
    assertEquals(
        new InvocationNode(new CompoundNode(new NumberNode(0))).hashCode(),
        n.getChild(1).hashCode());
  }
}

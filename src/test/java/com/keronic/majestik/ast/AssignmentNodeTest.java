package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class AssignmentNodeTest extends NodeTest {
  @Test
  void testEquals() {
    var node1 = new AssignmentNode(new VariableNode(0), new CompoundNode());
    var node2 = new AssignmentNode(new VariableNode(0), new CompoundNode());

    assertEquals(node1, node2);

    assertNotEquals(node1, new CompoundNode());
    assertNotEqualsNull(node2);
  }

  @Test
  void testHashCode() {
    var node1 = new AssignmentNode(new VariableNode(0), new CompoundNode());
    var node2 = new AssignmentNode(new VariableNode(0), new CompoundNode());

    assertEquals(node1.hashCode(), node2.hashCode());
  }

  @Test
  void testToString() {
    var node = new AssignmentNode(new VariableNode(0), new NoOperationNode());
    assertEquals("AssignmentNode{lhs=VariableNode{idx=0},rhs=NoOperationNode{}}", node.toString());
  }
}

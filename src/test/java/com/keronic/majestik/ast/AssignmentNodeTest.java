package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class AssignmentNodeTest {
  @Test
  void testEquals() {
    var node1 = new AssignmentNode(new CompoundNode(), new CompoundNode());
    var node2 = new AssignmentNode(new CompoundNode(), new CompoundNode());

    assertEquals(node1, node2);

    assertNotEquals(node1, new CompoundNode());
    assertNotEquals(node2, null);
  }

  @Test
  void testHashCode() {
    var node1 = new AssignmentNode(new CompoundNode(), new CompoundNode());
    var node2 = new AssignmentNode(new CompoundNode(), new CompoundNode());

    assertEquals(node1.hashCode(), node2.hashCode());
  }
}

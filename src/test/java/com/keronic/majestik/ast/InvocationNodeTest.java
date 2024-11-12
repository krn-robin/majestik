package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

class InvocationNodeTest {
  /**
   * Verifies that InvocationNode instances with identical CompoundNode contents are considered equal.
   * Tests both empty CompoundNode and CompoundNode with a single NumberNode.
   */
  @Test
  void shouldConsiderInvocationNodesWithIdenticalCompoundNodesEqual() {
    assertEquals(new InvocationNode(new CompoundNode()), new InvocationNode(new CompoundNode()));
    assertEquals(new InvocationNode(new CompoundNode(new NumberNode(0))),
      new InvocationNode(new CompoundNode(new NumberNode(0))));
  }

  @Test
  void shouldNotConsiderDifferentInvocationNodesEqual() {
    InvocationNode node1 = new InvocationNode(new CompoundNode(new NumberNode(1)));
    InvocationNode node2 = new InvocationNode(new CompoundNode(new NumberNode(2)));
    assertNotEquals(node1, node2);
  }

  @Test
  void testHashcode() {
    assertEquals(new InvocationNode(new CompoundNode()).hashCode(),
      new InvocationNode(new CompoundNode()).hashCode());
  }
}

package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class IdentifierNodeTest extends NodeTest {
  @Test
  void testEquals() {
    var node1 = new IdentifierNode("var");
    var node2 = new IdentifierNode("var");
    var node3 = new IdentifierNode("other");

    // Reflexivity
    assertEquals(node1, node1);

    // Symmetry
    assertEquals(node1, node2);
    assertEquals(node2, node1);

    // Transitivity
    var node4 = new IdentifierNode("var");
    assertEquals(node1, node2);
    assertEquals(node2, node4);
    assertEquals(node1, node4);

    assertNotEquals(node1, node3);
    assertNotEqualsNull(node2);
    assertNotEquals(node3, new StringNode("other"));

    // Verify equals-hashCode contract
    assertTrue(node1.equals(node2) && node1.hashCode() == node2.hashCode());
  }

  @Test
  void testHashCode() {
    var node1 = new IdentifierNode("var");
    var node2 = new IdentifierNode("var");
    var node3 = new IdentifierNode("other");

    assertEquals(node1.hashCode(), node2.hashCode());
    assertNotEquals(node1.hashCode(), node3.hashCode());

    // Verify hash code consistency
    int initialHashCode = node1.hashCode();
    assertEquals(initialHashCode, node1.hashCode());
  }
}

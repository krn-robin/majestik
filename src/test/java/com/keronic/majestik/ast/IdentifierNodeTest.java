package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class IdentifierNodeTest {
  @Test
  void testEquals() {
    var node1 = new IdentifierNode("var");
    var node2 = new IdentifierNode("var");
    var node3 = new IdentifierNode("other");

    assertEquals(node1, node2);

    assertNotEquals(node1, node3);
    assertNotEquals(node2, null);
    assertNotEquals(node3, new StringNode("other"));
  }

  void testHashCode() {
    var node1 = new IdentifierNode("var");
    var node2 = new IdentifierNode("var");
    var node3 = new IdentifierNode("other");

    assertEquals(node1.hashCode(), node2.hashCode());
    assertNotEquals(node1.hashCode(), node3.hashCode());
  }
}

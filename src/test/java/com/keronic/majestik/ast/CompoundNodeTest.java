package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for CompoundNode class, verifying equality and hash code functionality for AST
 * compound nodes.
 */
class CompoundNodeTest {
  @Test
  void testEquals() {
    // Test empty nodes
    assertEquals(new CompoundNode(), new CompoundNode());

    // Test nodes with same children
    assertEquals(new CompoundNode(new NumberNode(5)), new CompoundNode(new NumberNode(5)));

    // Test inequality with different children
    CompoundNode node1 = new CompoundNode(new NumberNode(5));
    CompoundNode node2 = new CompoundNode(new NumberNode(10));
    assertNotEquals(node1, node2);

    // Test inequality with different number of children
    CompoundNode node3 = new CompoundNode(new NumberNode(5), new NumberNode(10));
    assertNotEquals(node1, node3);

    // Test inequality with null
    assertNotEquals(node1, null);

    // Test inequality with different type
    assertNotEquals(node1, new NumberNode(5));
  }

  @Test
  void testHashCode() {
    // Test empty nodes
    CompoundNode empty1 = new CompoundNode();
    CompoundNode empty2 = new CompoundNode();
    assertEquals(empty1.hashCode(), empty2.hashCode());

    // Test nodes with same children
    CompoundNode node1 = new CompoundNode(new NumberNode(5));
    CompoundNode node2 = new CompoundNode(new NumberNode(5));
    assertEquals(node1.hashCode(), node2.hashCode());

    // Verify hash code contract
    assertEquals(node1, node2);
    assertEquals(node1.hashCode(), node2.hashCode(), "Equal objects must have equal hash codes");

    // Test hash codes for unequal objects
    CompoundNode node3 = new CompoundNode(new NumberNode(10));
    CompoundNode node4 = new CompoundNode(new NumberNode(5), new NumberNode(10));
    // Note: Different hash codes for unequal objects is not guaranteed by the contract
    // but it's good practice for hash code distribution
    assertNotEquals(
        node1.hashCode(),
        node3.hashCode(),
        "Different values should likely have different hash codes");
    assertNotEquals(
        node1.hashCode(),
        node4.hashCode(),
        "Different number of children should likely have different hash codes");
  }

  @Test
  void testToString() {
    var node1 = new CompoundNode();
    var node2 = new CompoundNode(node1, new CompoundNode());
    var node3 = new CompoundNode(node2, new CompoundNode());

    assertTrue(node1.toString().startsWith("com.keronic.majestik.ast.CompoundNode@"));
    assertTrue(node1.toString().endsWith(" ()"));

    assertTrue(node2.toString().startsWith("com.keronic.majestik.ast.CompoundNode@"));
    assertTrue(node2.toString().endsWith(String.format(" ((%s))", node1.toString())));

    assertTrue(node3.toString().startsWith("com.keronic.majestik.ast.CompoundNode@"));
    assertTrue(node3.toString().endsWith(String.format(" ((%s)(%s))", node1.toString(), node1.toString())));
  }
}

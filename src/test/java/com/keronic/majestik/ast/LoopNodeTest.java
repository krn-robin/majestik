package com.keronic.majestik.ast;

import module java.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the LoopNode class, which implements the Magik loop construct. These tests verify
 * the equality, hash code, and string representation behavior of loop nodes.
 */
class LoopNodeTest extends NodeTest {
  @Test
  void shouldBeEqualWhenValuesAreTheSame() {
    var node1 = new LoopNode("");
    var node2 = new LoopNode("");
    var node3 = new LoopNode("");
    var node4 = new LoopNode("named");

    // Equality
    assertEquals(node1, node2);

    // Reflexivity
    assertEquals(node1, node1);

    // Symmetry
    assertEquals(node1, node2);
    assertEquals(node2, node1);

    // Transitivity
    assertEquals(node1, node2);
    assertEquals(node2, node3);
    assertEquals(node1, node3);

    // Inequality
    assertNotEqualsNull(node1);
    assertNotEquals(node1, "");
    assertNotEquals(node1, node4);
    assertNotEquals(node1, "named");
  }

  @Test
  void shouldHaveSameHashCodeWhenValuesAreEqual() {
    var node1 = new LoopNode("");
    var node2 = new LoopNode("");
    var node3 = new LoopNode("named");

    // Test consistency
    assertEquals(node1.hashCode(), node1.hashCode(), "Hash code should be consistent");

    // Test with compound expression
    var compound = new CompoundNode(node1, node2);
    assertNotEquals(node1.hashCode(), compound.hashCode());

    assertEquals(node1.hashCode(), node2.hashCode());

    // Test inequality
    assertNotEquals(node1.hashCode(), node3.hashCode());
  }

  @Test
  void testToString() {
    assertEquals("LoopNode{name='named',children=[]}", new LoopNode("named").toString());
    assertEquals("LoopNode{name='',children=[]}", new LoopNode("").toString());

    var nodeWithChildren = new LoopNode("", new CompoundNode());
    assertEquals(
        "LoopNode{name='',children=[CompoundNode{children=[]}]}", nodeWithChildren.toString());
  }
}

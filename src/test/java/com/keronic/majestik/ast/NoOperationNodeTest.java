package com.keronic.majestik.ast;

import module java.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link NoOperationNode} class.
 */
class NoOperationNodeTest extends NodeTest {
  @Test
  void shouldBeEqualWhenValuesAreTheSame() {
    var node1 = new NoOperationNode();
    var node2 = new NoOperationNode();
    var node3 = new NoOperationNode();

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
    assertNotEquals(node1, true);
    assertNotEqualsNull(node1);
  }

  @Test
  void shouldHaveSameHashCodeWhenValuesAreEqual() {
    var node1 = new NoOperationNode();
    var node2 = new NoOperationNode();
    assertEquals(node1.hashCode(), node2.hashCode());
  }

  @Test
  void shouldGenerateNopInstruction() {
    var cnode = new NoOperationNode();
    var code = this.compileInto(cnode::compileInto);

    assertEquals(1, code.elementList().size());
    var cel = code.elementList().getFirst();

    assertInstanceOf(NopInstruction.class, cel);
  }

  @Test
  void testToString() {
    assertEquals("NoOperationNode{}", new NoOperationNode().toString());
  }
}

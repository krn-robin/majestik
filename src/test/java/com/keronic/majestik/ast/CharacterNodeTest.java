package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class CharacterNodeTest extends NodeTest {
  @Test
  void shouldBeEqualWhenValuesAreTheSame() {
    var node1 = new CharacterNode('a');
    var node2 = new CharacterNode('a');

    assertEquals(node1, node2);

    assertNotEquals(node1, 'a');
    var node3 = new CharacterNode('b');
    assertNotEquals(node1, node3);
    var node4 = new CharacterNode(' ');
    assertNotEqualsNull(node4);
  }

  @Test
  void shouldHaveSameHashCodeWhenValuesAreEqual() {
    var node1 = new CharacterNode('a');
    var node2 = new CharacterNode('a');
    var node3 = new CharacterNode('b');

    // Test consistency
    assertEquals(node1.hashCode(), node1.hashCode(), "Hash code should be consistent");

    // Test with compound expression
    var compound = new CompoundNode(node1, node2);
    assertNotEquals(node1.hashCode(), compound.hashCode());

    assertEquals(node1.hashCode(), node2.hashCode());

    // Test inequality
    assertNotEquals(node1.hashCode(), node3.hashCode());
  }
}

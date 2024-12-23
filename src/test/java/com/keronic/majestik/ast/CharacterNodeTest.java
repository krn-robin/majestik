package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import module java.base;

import org.junit.jupiter.api.Test;

public class CharacterNodeTest extends NodeTest {
  @Test
  void shouldBeEqualWhenValuesAreTheSame() {
    var node1 = new CharacterNode('a');
    var node2 = new CharacterNode('a');

    assertEquals(node1, node2);

    assertNotEquals(node1, (char)'a');
    var node3 = new CharacterNode('b');
    assertNotEquals(node1, node3);
    var node4 = new CharacterNode((char)' ');
    assertNotEqualsNull(node4);
  }
}

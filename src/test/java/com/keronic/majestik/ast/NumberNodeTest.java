package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

class NumberNodeTest {
  @Test
  void shouldBeEqualWhenValuesAreTheSame() {
    var node1 = new NumberNode(5);
    var node2 = new NumberNode(5);
    assertEquals(node1, node2);

    // Test inequality
    var node3 = new NumberNode(10);
    assertNotEquals(node1, node3);
    var node4 = new NumberNode(0);
    assertNotEquals(node4, null);
  }

  @Test
  void shouldHaveSameHashCodeWhenValuesAreEqual() {
    var node1 = new NumberNode(5);
    var node2 = new NumberNode(5);
    assertEquals(node1.hashCode(), node2.hashCode());
  }
}

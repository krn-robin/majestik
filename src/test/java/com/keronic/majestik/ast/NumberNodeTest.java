package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NumberNodeTest {
  @Test
  void shouldBeEqualWhenValuesAreTheSame() {
    assertEquals(new NumberNode(5), new NumberNode(5));
  }

  @Test
  void shouldHaveSameHashCodeWhenValuesAreEqual() {
    assertEquals(new NumberNode(5).hashCode(), new NumberNode(5).hashCode());
  }
}

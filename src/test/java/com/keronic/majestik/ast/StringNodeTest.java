package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StringNodeTest {
  @Test
  void shouldBeEqualWhenValuesAreTheSame() {

    assertEquals(new StringNode("string1"), new StringNode("string1"));
  }

  @Test
  void shouldHaveSameHashCodeWhenValuesAreEqual() {
    assertEquals(new StringNode("string2").hashCode(), new StringNode("string2").hashCode());
  }
}

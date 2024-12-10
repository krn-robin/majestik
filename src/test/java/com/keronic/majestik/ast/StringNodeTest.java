package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;

class StringNodeTest {
  @Test
  @SuppressWarnings("java:S3415")
  void shouldBeEqualWhenValuesAreTheSame() {
    var node1 = new StringNode("string1");
    var node2 = new StringNode("string1");

    assertEquals(node1, node2);

    // Test inequality
    assertNotEquals(node1, "string1");
    var node3 = new StringNode("String2");
    assertNotEquals(node1, node3);
    var node4 = new StringNode("");
    assertNotEquals(node4, null);
  }

  @Test
  void shouldHaveSameHashCodeWhenValuesAreEqual() {
    assertEquals(new StringNode("string2").hashCode(), new StringNode("string2").hashCode());
  }
}

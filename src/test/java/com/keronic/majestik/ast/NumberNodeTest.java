package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class NumberNodeTest {
  @Test
  void testEquals() {
    assertEquals(new NumberNode(5), new NumberNode(5));
  }
}

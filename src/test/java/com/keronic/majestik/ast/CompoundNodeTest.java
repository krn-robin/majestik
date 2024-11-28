package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CompoundNodeTest {
  @Test
  void testEquals() {
    assertEquals(new CompoundNode(), new CompoundNode());
    assertEquals(new CompoundNode(new NumberNode(5)), new CompoundNode(new NumberNode(5)));
  }
}

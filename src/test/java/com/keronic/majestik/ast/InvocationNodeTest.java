package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class InvocationNodeTest {
  @Test
  void testEquals() {
    assertEquals(new InvocationNode(new CompoundNode()), new InvocationNode(new CompoundNode()));
    assertEquals(
        new InvocationNode(new CompoundNode(new NumberNode(0))),
        new InvocationNode(new CompoundNode(new NumberNode(0))));
  }
}

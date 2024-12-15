package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class IfExpressionNodeTest extends NodeTest {
  void shouldBeEqualWhenValuesAreTheSame() {
    var node1 = new IfExpressionNode(new BooleanNode(true), new CompoundNode(), new CompoundNode());
    var node2 = new IfExpressionNode(new BooleanNode(true), new CompoundNode(), new CompoundNode());
    var node3 = new IfExpressionNode(new BooleanNode(true), new CompoundNode(), new CompoundNode());
    var node4 =
        new IfExpressionNode(new BooleanNode(false), new CompoundNode(), new CompoundNode());

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
    assertNotEquals(node1, node4);
    assertNotEqualsNull(node4);
  }

  @Test
  void shouldHaveSameHashCodeWhenValuesAreEqual() {
    var node1 = new IfExpressionNode(new BooleanNode(true), new CompoundNode(), new CompoundNode());
    var node2 = new IfExpressionNode(new BooleanNode(true), new CompoundNode(), new CompoundNode());
    var node3 =
        new IfExpressionNode(new BooleanNode(false), new CompoundNode(), new CompoundNode());
    assertEquals(node1.hashCode(), node2.hashCode());

    // Test inequality
    assertNotEquals(node1.hashCode(), node3.hashCode());
  }
}

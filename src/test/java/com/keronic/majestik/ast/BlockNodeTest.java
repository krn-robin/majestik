package com.keronic.majestik.ast;

import module java.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BlockNodeTest extends NodeTest {
  @Test
  @DisplayName("Boolean node equality tests")
  void shouldBeEqualWhenValuesAreTheSame() {
    var node1 = new BlockNode(new BooleanNode(true));
    var node2 = new BlockNode(new BooleanNode(true));
    var node3 = new BlockNode(new BooleanNode(true));
    var node4 = new BlockNode(new BooleanNode(false));

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
    var node1 = new BlockNode(new BooleanNode(true));
    var node2 = new BlockNode(new BooleanNode(true));
    var node3 = new BlockNode(new BooleanNode(false));

    // Test consistency
    assertEquals(node1.hashCode(), node1.hashCode(), "Hash code should be consistent");

    // Test with compound expression
    var compound = new CompoundNode(node1, node2);
    assertNotEquals(node1.hashCode(), compound.hashCode());

    assertEquals(node1.hashCode(), node2.hashCode());

    // Test inequality
    assertNotEquals(node1.hashCode(), node3.hashCode());
  }

  @Test
  void shouldGenerateNoInstructions() {
    var cnode = new BlockNode(new NoOperationNode());
    var code = this.compileInto(cnode::compileInto);

    assertEquals(1, code.elementList().size());
    var instruction = code.elementList().getFirst();
    assertInstanceOf(NopInstruction.class, instruction);
  }

  @Test
  void testToString() {
    assertEquals(
        "BlockNode{children=[NoOperationNode{}]}", new BlockNode(new NoOperationNode()).toString());
  }
}

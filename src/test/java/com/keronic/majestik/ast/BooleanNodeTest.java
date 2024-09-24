package com.keronic.majestik.ast;

import module java.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class BooleanNodeTest extends NodeTest {
  @Test
  void shouldBeEqualWhenValuesAreTheSame() {
    var node1 = new BooleanNode(true);
    var node2 = new BooleanNode(true);
    var node3 = new BooleanNode(true);
    var node4 = new BooleanNode(false);

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
    var node1 = new BooleanNode(true);
    var node2 = new BooleanNode(true);
    var node3 = new BooleanNode(false);
    assertEquals(node1.hashCode(), node2.hashCode());

    // Test inequality
    assertNotEquals(node1.hashCode(), node3.hashCode());
  }

  @Test
  void shouldGenerateFieldInstructions() {
    var cnode = new CompoundNode(new BooleanNode(true), new BooleanNode(false));
    var code = this.compileInto(cnode::compileInto);

    assertEquals(2, code.elementList().size());
    var trueinstruction = code.elementList().get(0);
    var falseinstruction = code.elementList().get(1);
    assertInstanceOf(FieldInstruction.class, trueinstruction);
    assertInstanceOf(FieldInstruction.class, falseinstruction);
    var truefis = (FieldInstruction) trueinstruction;
    var falsefis = (FieldInstruction) falseinstruction;

    // Verify instruction properties
    assertEquals("java/lang/Boolean", truefis.owner().name().toString());
    assertEquals("TRUE", truefis.name().toString());
    assertEquals("java/lang/Boolean", falsefis.owner().name().toString());
    assertEquals("FALSE", falsefis.name().toString());
  }
}

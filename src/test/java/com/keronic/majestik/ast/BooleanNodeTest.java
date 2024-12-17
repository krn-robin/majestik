package com.keronic.majestik.ast;

import module java.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class BooleanNodeTest extends NodeTest {
  @Test
  @DisplayName("Boolean node equality tests")
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

  @ParameterizedTest
  @CsvSource({"true,  BooleanNode{value=true}", "false, BooleanNode{value=false}"})
  void testToString(boolean input, String expected) {
    assertEquals(expected, new BooleanNode(input).toString());
  }
}

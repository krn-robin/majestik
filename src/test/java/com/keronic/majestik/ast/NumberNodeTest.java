package com.keronic.majestik.ast;

import module java.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.classfile.instruction.ConstantInstruction;
import java.lang.classfile.instruction.InvokeInstruction;
import org.junit.jupiter.api.Test;

class NumberNodeTest extends NodeTest {
  @Test
  void shouldBeEqualWhenValuesAreTheSame() {
    var node1 = new NumberNode(5);
    var node2 = new NumberNode(5);
    assertEquals(node1, node2);

    // Test inequality
    assertNotEquals(node1, 5);
    var node3 = new NumberNode(10);
    assertNotEquals(node1, node3);
    var node4 = new NumberNode(0);
    assertNotEqualsNull(node4);
  }

  @Test
  void shouldHaveSameHashCodeWhenValuesAreEqual() {
    var node1 = new NumberNode(5);
    var node2 = new NumberNode(5);
    assertEquals(node1.hashCode(), node2.hashCode());

    // Test inequality
    var node3 = new NumberNode(10);
    assertNotEquals(node1.hashCode(), node3.hashCode());
  }

  @Test
  void shouldGenerateCorrectBytecodeForNumericLiterals() {
    final int EXPECTED_INSTRUCTION_COUNT = 4;
    final int LONG_CONSTANT_INDEX = 0;
    final int LONG_INVOKE_INDEX = 1;
    final int DOUBLE_CONSTANT_INDEX = 2;
    final int DOUBLE_INVOKE_INDEX = 3;

    var compoundNode = new CompoundNode(new NumberNode(1L), new NumberNode(2d));
    var code = this.compileInto(compoundNode::compileInto);

    assertEquals(EXPECTED_INSTRUCTION_COUNT, code.elementList().size());
    var instruction0 = code.elementList().get(LONG_CONSTANT_INDEX);
    var instruction1 = code.elementList().get(LONG_INVOKE_INDEX);
    var instruction2 = code.elementList().get(DOUBLE_CONSTANT_INDEX);
    var instruction3 = code.elementList().get(DOUBLE_INVOKE_INDEX);

    assertInstanceOf(ConstantInstruction.class, instruction0);
    assertInstanceOf(InvokeInstruction.class, instruction1);
    assertInstanceOf(ConstantInstruction.class, instruction2);
    assertInstanceOf(InvokeInstruction.class, instruction3);

    var consins0 = (ConstantInstruction) instruction0;
    assertEquals(TypeKind.LongType, consins0.typeKind());
    var consins1 = (ConstantInstruction) instruction2;
    assertEquals(TypeKind.DoubleType, consins1.typeKind());

    var invins0 = (InvokeInstruction) instruction1;
    assertEquals("valueOf", invins0.name().toString());
    assertEquals("(J)Ljava/lang/Long;", invins0.type().toString());

    var invins1 = (InvokeInstruction) instruction3;
    assertEquals("valueOf", invins1.name().toString());
    assertEquals("(D)Ljava/lang/Double;", invins1.type().toString());
  }
}

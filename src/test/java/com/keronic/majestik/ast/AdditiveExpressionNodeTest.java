package com.keronic.majestik.ast;

import module java.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class AdditiveExpressionNodeTest extends NodeTest {
  @Test
  void testEquals() {
    final var node1 = new AdditiveExpressionNode(new VariableNode(0), new VariableNode(1));
    final var node2 = new AdditiveExpressionNode(new VariableNode(0), new VariableNode(1));
    final var node3 = new AdditiveExpressionNode(new VariableNode(0), new VariableNode(2));

    assertEquals(node1, node2);

    assertNotEquals(node1, node3);
    assertNotEqualsNull(node2);
  }

  @Test
  void testHashCode() {
    final var node1 = new AdditiveExpressionNode(new VariableNode(0), new CompoundNode());
    final var node2 = new AdditiveExpressionNode(new VariableNode(0), new CompoundNode());

    assertEquals(node1.hashCode(), node2.hashCode());
  }

  @Test
  void testToString() {
    final var node = new AdditiveExpressionNode(new VariableNode(1), new NoOperationNode());
    assertEquals(
        "AdditiveExpressionNode{lhs=VariableNode{idx=1},rhs=NoOperationNode{}}", node.toString());
  }

  @Test
  void shouldGenerateInvokeDynamicInstruction() {
    final var cnode = new AdditiveExpressionNode(new NumberNode(1l), new NumberNode(2l));

    final var code = this.compileInto(cnode::compileInto);
    assertEquals(5, code.elementList().size());
    final var cel = code.elementList().toArray(new CodeElement[0]);

    final var consins0 = (ConstantInstruction) cel[0];
    assertEquals(TypeKind.LongType, consins0.typeKind());
    final var consins1 = (ConstantInstruction) cel[2];
    assertEquals(TypeKind.LongType, consins1.typeKind());

    final var invins0 = (InvokeInstruction) cel[1];
    assertEquals("valueOf", invins0.name().toString());
    assertEquals("(J)Ljava/lang/Long;", invins0.type().toString());

    final var invins1 = (InvokeInstruction) cel[3];
    assertEquals("valueOf", invins1.name().toString());
    assertEquals("(J)Ljava/lang/Long;", invins1.type().toString());

    assertInstanceOf(InvokeDynamicInstruction.class, cel[4]);
    final var indy = (InvokeDynamicInstruction) cel[4];

    // Verify instruction properties
    assertEquals(
        "MethodHandleDesc[STATIC/BinaryDispatcher::bootstrap(MethodHandles$Lookup,String,MethodType)CallSite]",
        indy.bootstrapMethod().toString());
    assertEquals("+", indy.name().toString());
  }
}

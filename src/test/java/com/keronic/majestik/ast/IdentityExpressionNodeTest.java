package com.keronic.majestik.ast;

import module java.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class IdentityExpressionNodeTest extends NodeTest {
  @Test
  void testEquals() {
    final var node1 = new IdentityExpressionNode(new VariableNode(0), new VariableNode(1));
    final var node2 = new IdentityExpressionNode(new VariableNode(0), new VariableNode(1));
    final var node3 = new IdentityExpressionNode(new VariableNode(0), new VariableNode(2));

    assertEquals(node1, node2);

    assertNotEquals(node1, node3);
    assertNotEqualsNull(node2);
  }

  @Test
  void testHashCode() {
    final var node1 = new IdentityExpressionNode(new VariableNode(0), new CompoundNode());
    final var node2 = new IdentityExpressionNode(new VariableNode(0), new CompoundNode());

    assertEquals(node1.hashCode(), node2.hashCode());
  }

  @Test
  void testToString() {
    final var node = new IdentityExpressionNode(new VariableNode(1), new NoOperationNode());
    assertEquals(
        "IdentityExpressionNode{lhs=VariableNode{idx=1},rhs=NoOperationNode{}}", node.toString());
  }

  @Test
  void shouldGenerateInvokeDynamicInstruction() {
    final var cnode = new IdentityExpressionNode(new NumberNode(1l), new NumberNode(2l));

    final var code = this.compileInto(cnode::compileInto);
    assertEquals(9, code.elementList().size());
    final var cel = code.elementList().toArray(new CodeElement[0]);

    final var consins0 = (ConstantInstruction) cel[0];
    assertEquals(TypeKind.LongType, consins0.typeKind());
    final var consins1 = (ConstantInstruction) cel[2];
    assertEquals(TypeKind.LongType, consins1.typeKind());

    final var invins0 = (InvokeInstruction) cel[1];
    assertEquals("valueOf", invins0.name().toString());
    assertEquals("java/lang/Long", invins0.owner().asInternalName());

    final var invins1 = (InvokeInstruction) cel[3];
    assertEquals("valueOf", invins1.name().toString());
    assertEquals("java/lang/Long", invins1.owner().asInternalName());

    final var invins2 = (InvokeInstruction) cel[4];
    assertEquals("is", invins2.name().toString());
    assertEquals(
        "com/keronic/majestik/language/utils/MagikObjectUtils", invins2.owner().asInternalName());

    assertInstanceOf(NewObjectInstruction.class, cel[5]);
    assertInstanceOf(StackInstruction.class, cel[6]);
    assertInstanceOf(StackInstruction.class, cel[7]);
    assertInstanceOf(InvokeInstruction.class, cel[8]);

    final var invins3 = (InvokeInstruction) cel[8];
    assertEquals(ConstantDescs.INIT_NAME, invins3.name().toString());
    assertEquals("java/lang/Boolean", invins3.owner().asInternalName());
  }
}

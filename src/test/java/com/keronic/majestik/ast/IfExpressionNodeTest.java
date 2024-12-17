package com.keronic.majestik.ast;

import module java.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class IfExpressionNodeTest extends NodeTest {
  @Test
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
    var node4 =
        new IfExpressionNode(new BooleanNode(true), new NumberNode(42), new StringNode("else"));
    assertEquals(node1.hashCode(), node2.hashCode());

    // Test inequality
    assertNotEquals(node1.hashCode(), node3.hashCode());

    // Test hash code consistency
    assertEquals(node4.hashCode(), node4.hashCode(), "Hash code should be consistent");
  }

  @Test
  void testToString() {
    var node = new IfExpressionNode(new BooleanNode(true), new CompoundNode(), new CompoundNode());
    assertEquals(
        "IfExpressionNode{condition=BooleanNode{value=true},body=CompoundNode{children=[]},else=CompoundNode{children=[]}}",
        node.toString());
  }

  @Test
  void shouldGenerateBranchInstructions() {
    var nnode = new NoOperationNode();
    var node = new IfExpressionNode(new BooleanNode(true), nnode, nnode);
    var cnode = new CompoundNode(node, nnode);
    var code = this.compileInto(cnode::compileInto);

    assertEquals(9, code.elementList().size());
    var cel = code.elementList().toArray(new CodeElement[0]);

    assertInstanceOf(FieldInstruction.class, cel[0]);
    assertInstanceOf(InvokeInstruction.class, cel[1]);
    assertInstanceOf(BranchInstruction.class, cel[2]);
    assertInstanceOf(NopInstruction.class, cel[3]);
    assertInstanceOf(BranchInstruction.class, cel[4]);
    assertInstanceOf(LabelTarget.class, cel[5]);
    assertInstanceOf(NopInstruction.class, cel[6]);
    assertInstanceOf(LabelTarget.class, cel[7]);
    assertInstanceOf(NopInstruction.class, cel[8]);

    var invoke = (InvokeInstruction) cel[1];
    assertEquals("should_be_boolean", invoke.name().toString());

    var branch0 = (BranchInstruction) cel[2];
    assertEquals(cel[5], branch0.target());
    assertEquals(Opcode.IFEQ, branch0.opcode());

    var branch1 = (BranchInstruction) cel[4];
    assertEquals(cel[7], branch1.target());
    assertEquals(Opcode.GOTO, branch1.opcode());
  }
}

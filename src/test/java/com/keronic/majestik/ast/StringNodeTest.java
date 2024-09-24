package com.keronic.majestik.ast;

import module java.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class StringNodeTest extends NodeTest {
  @Test
  void shouldBeEqualWhenValuesAreTheSame() {
    var node1 = new StringNode("string1");
    var node2 = new StringNode("string1");

    assertEquals(node1, node2);

    // Test inequality
    assertNotEquals(node1, "string1");
    var node3 = new StringNode("String2");
    assertNotEquals(node1, node3);
    var node4 = new StringNode("");
    assertNotEqualsNull(node4);
  }

  @Test
  void shouldHaveSameHashCodeWhenValuesAreEqual() {
    var node1 = new StringNode("string2");
    var node2 = new StringNode("string2");

    // Test consistency
    int hash1 = node1.hashCode();
    assertEquals(hash1, node1.hashCode(), "hashCode should be consistent");

    // Test equality contract
    assertEquals(node1.hashCode(), node2.hashCode(), "Equal objects should have equal hashCodes");

    // Verify hashCode/equals consistency
    if (node1.equals(node2)) {
      assertEquals(node1.hashCode(), node2.hashCode(), "Equal objects must have equal hashCodes");
    }
  }

  @Test
  void shouldGenerateInvokeDynamicInstruction() {
    var cnode = new CompoundNode(new StringNode("a test string"));

    var code = this.compileInto(cnode::compileInto);

    assertEquals(1, code.elementList().size());
    var instruction = code.elementList().getFirst();
    assertInstanceOf(InvokeDynamicInstruction.class, instruction);
    var indy = (InvokeDynamicInstruction) instruction;

    // Verify instruction properties
    assertEquals(
        "MethodHandleDesc[STATIC/ConstantBuilder::stringBootstrap(MethodHandles$Lookup,String,MethodType,String)CallSite]",
        indy.bootstrapMethod().toString());
    assertEquals("[a test string]", indy.bootstrapArgs().toString());
  }
}

package com.keronic.majestik.ast;

import module java.base;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

class StringNodeTest extends NodeTest {
  @Test
  // Suppressing "Assertion arguments should be passed in the correct order" as the expected/actual order is correct here
  @SuppressWarnings("java:S3415")
  void shouldBeEqualWhenValuesAreTheSame() {
    var node1 = new StringNode("string1");
    var node2 = new StringNode("string1");

    assertEquals(node1, node2);

    // Test inequality
    assertNotEquals(node1, "string1");
    var node3 = new StringNode("String2");
    assertNotEquals(node1, node3);
    var node4 = new StringNode("");
    assertNotEquals(node4, null);
  }

  @Test
  void shouldHaveSameHashCodeWhenValuesAreEqual() {
    assertEquals(new StringNode("string2").hashCode(), new StringNode("string2").hashCode());
  }

  @Test
  void shouldGenerateInvokeDynamicInstruction() {
    var cnode = new CompoundNode(new StringNode("a test string"));

    Consumer<CodeBuilder> cb = xb -> cnode.compileInto(xb);

    var code = this.compileInto(cb);

    assertEquals(1, code.elementList().size());
    var instruction = code.elementList().getFirst();
    assertInstanceOf(InvokeDynamicInstruction.class, instruction);
    var indy = (InvokeDynamicInstruction) instruction;
    assertEquals(
        "MethodHandleDesc[STATIC/ConstantBuilder::stringBootstrap(MethodHandles$Lookup,String,MethodType,String)CallSite]",
        indy.bootstrapMethod().toString());
    assertEquals("[a test string]", indy.bootstrapArgs().toString());
  }
}

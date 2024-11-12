package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertEquals;

import nl.ramsolutions.sw.magik.MagikFile;
import org.junit.jupiter.api.Test;

class MajestikCodeVisitorTest {
  @Test
  void testVisitString() {
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("\"string1\"%n'string2'%n").formatted());

    // Execute
    var n = (CompoundNode) mcv.scanFile(mf);

    // Verify
    assertEquals(2, n.getChildCount());
    assertEquals(new StringNode("string1"), n.getChild(0));
    assertEquals(new StringNode("string2"), n.getChild(1));
  }

  @Test
  void testVisitBlock_statement() {
    // Test input

    // Execute

    // This method primarily logs output, so you might want to check the logs if
    // necessary
    // For example, using a logging framework that supports capturing logs in tests
  }

  @Test
  void testVisitInvoke() {
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("write(0)%n").formatted());

    // Execute
    var n = (CompoundNode) mcv.scanFile(mf);

    // Verify
    assertEquals(1, n.getChildCount());
    n = (CompoundNode) n.getChild(0);
    assertEquals(new IdentifierNode("write"), n.getChild(0));
    // assertEquals(new InvocationNode(new CompoundNode(new NumberNode(0))), n.getChild(1));
  }
}

package com.keronic.majestik.ast;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import nl.ramsolutions.sw.magik.MagikFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MajestikCodeVisitorTest {
  @BeforeEach
  public void setUp() {}

  @Test
  void testVisitString() {
    var mcv = new MajestikCodeVisitor();
    var mf = new MagikFile(MagikFile.DEFAULT_URI, ("\"string1\"%n" + "\"string2\"%n").formatted());

    // Execute
    var n = (CompoundNode) mcv.scanFile(mf);

    // Verify
    var expected = new Node[] {new StringNode("string1"), new StringNode("string2")};
    assertArrayEquals(expected, n.getChildren());
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
    // Test input

    // Execute

    // Verify
  }
}

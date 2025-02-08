package com.keronic.majestik.ast;

import module java.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CompilationContextTest {

  @Test
  void testLabels() {
    // Test input
    CompilationContext cc = new CompilationContext(null);

    assertThrows(NoSuchElementException.class, () -> cc.lastLabel());

    cc.bindLabel("outer", null, null);
    assertEquals("outer", cc.lastLabel().name());
    cc.bindLabel("inner", null, null);
    assertEquals("inner", cc.lastLabel().name());

    var olabel = cc.findLabel("outer");
    assertEquals("outer", olabel.name());

    var ilabel = cc.findLabel("inner");
    assertEquals("inner", ilabel.name());

    assertThrows(NoSuchElementException.class, () -> cc.findLabel("unknown"));

    cc.popLabel();
    assertEquals("outer", cc.lastLabel().name());

    cc.popLabel();
    assertThrows(NoSuchElementException.class, () -> cc.lastLabel());
  }
}

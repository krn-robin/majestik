package com.keronic.majestik.ast;

import module java.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CompilationContextTest {

  @Test
  void shouldThrowExceptionWhenContextIsEmpty() {
    CompilationContext cc = new CompilationContext(null);
    assertThrows(NoSuchElementException.class, () -> cc.lastLabel());
  }

  @Test
  void shouldManageLabelsInLastInFirstOutOrder() {
    CompilationContext cc = new CompilationContext(null);
    cc.bindLabel("outer", null, null);
    cc.bindLabel("inner", null, null);
    assertEquals("inner", cc.lastLabel().name());

    cc.popLabel();
    assertEquals("outer", cc.lastLabel().name());

    cc.popLabel();
    assertThrows(NoSuchElementException.class, () -> cc.popLabel());
  }

  @Test
  void shouldFindLabelsByName() {
    CompilationContext cc = new CompilationContext(null);
    cc.bindLabel("outer", null, null);
    cc.bindLabel("inner", null, null);

    var olabel = cc.findLabel("outer");
    assertEquals("outer", olabel.name());

    assertThrows(NoSuchElementException.class, () -> cc.findLabel("unknown"));
  }
}

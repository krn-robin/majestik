package com.keronic.majestik.language.utils;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExecutableMagikTest {

  private ExecutableMagik executableMagik;

  @BeforeEach
  void setUp() {
    executableMagik =
        new ExecutableMagik() {
          @Override
          public Object execute() {
            return "Execution Result";
          }
        };
  }

  @Test
  void shouldReturnCorrectExecutionResult() {
    assertEquals("Execution Result", executableMagik.execute());
  }

  @Test
  void shouldNotThrowExceptionWhenPreloading() {
    assertDoesNotThrow(() -> executableMagik.preload());
  }
}

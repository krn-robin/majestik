package com.keronic.majestik.language.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.keronic.majestik.MajestikRuntimeException;
import org.junit.jupiter.api.Test;

class MagikObjectUtilsTest {
  @Test
  void testShouldBeBoolean() {
    assertEquals(true, MagikObjectUtils.should_be_boolean(Boolean.TRUE));
    assertEquals(false, MagikObjectUtils.should_be_boolean(Boolean.FALSE));

    assertThrows(
        MajestikRuntimeException.class,
        () -> {
          MagikObjectUtils.should_be_boolean(null);
        });
  }
}

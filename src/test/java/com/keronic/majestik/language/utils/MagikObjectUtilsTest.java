package com.keronic.majestik.language.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

  @Test
  void testIs() {
    assertTrue(MagikObjectUtils.is(1l, 1l));
    assertTrue(MagikObjectUtils.is(null, null));
    assertFalse(MagikObjectUtils.is(Boolean.FALSE, Boolean.TRUE));
    assertFalse(MagikObjectUtils.is(0l, null));
  }
}

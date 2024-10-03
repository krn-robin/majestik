package com.keronic.majestik.language.utils;

import static org.junit.jupiter.api.Assertions.*;
import com.keronic.majestik.MajestikRuntimeException;
import org.junit.jupiter.api.Test;

class MagikObjectUtilsTest {

  @Test
  void testShouldBeBoolean() throws Throwable {
    assertSame(true, MagikObjectUtils.should_be_boolean(Boolean.TRUE));
    assertSame(false, MagikObjectUtils.should_be_boolean(Boolean.FALSE));

    assertThrows(
        MajestikRuntimeException.class,
        () -> {
          MagikObjectUtils.should_be_boolean(null);
        });
  }
}

package com.keronic.language.utils;

import static org.junit.jupiter.api.Assertions.*;
import com.keronic.majestik.MajestikRuntimeException;
import com.keronic.majestik.language.utils.MagikObjectUtils;
import org.junit.jupiter.api.Test;

public class MagikObjectUtilsTest {

  @Test
  public void testShouldBeBoolean() throws Throwable {
    assertSame(true, MagikObjectUtils.should_be_boolean(Boolean.TRUE));
    assertSame(false, MagikObjectUtils.should_be_boolean(Boolean.FALSE));

    assertThrows(
        MajestikRuntimeException.class,
        () -> {
          MagikObjectUtils.should_be_boolean(null);
        });
  }
}

/** */
package com.keronic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** */
class PathUtilsTest {
  @Test
  void testGetBaseName() {
    assertEquals("test", PathUtils.getBaseName("test.majestik"));
  }

  @Test
  void testGetBaseNameDoubleExt() {
    assertEquals("test.new", PathUtils.getBaseName("test.new.majestik"));
  }

  @Test
  void testGetBaseNameEmpty() {
    assertEquals("", PathUtils.getBaseName(""));
  }

  @Test
  void testGetBaseNameSpace() {
    assertEquals("test test", PathUtils.getBaseName("test test.ext"));
  }
}

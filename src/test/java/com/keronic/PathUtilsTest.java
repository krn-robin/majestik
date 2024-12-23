/** */
package com.keronic;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for the PathUtils class, focusing on path manipulation and file extension handling
 * functionality (e.g., extracting base names from files with single/double extensions, handling
 * special cases like empty strings and spaces).
 */
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

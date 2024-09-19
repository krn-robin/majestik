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
}

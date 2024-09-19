/** */
package com.keronic;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

/** */
public class PathUtilsTest {
	@Test
	public void testGetBaseName() {
		assertEquals("test", PathUtils.getBaseName("test.majestik"));
	}
}

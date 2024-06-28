/**
 *
 */
package com.keronic.test;

import static org.junit.Assert.*;

import com.keronic.PathUtils;

import org.junit.Test;

/**
 *
 */
public class PathUtilsTest {
	@Test
	public void testGetBaseName() {
		assertEquals("test", PathUtils.getBaseName("test.majestik"));
	}
}

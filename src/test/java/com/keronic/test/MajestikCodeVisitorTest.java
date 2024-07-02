package com.keronic.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.classfile.CodeBuilder;
import java.lang.classfile.constantpool.InvokeDynamicEntry;

import com.keronic.MajestikCodeVisitor;
import com.keronic.antlr4.MajestikParser;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("Test class not ready yet")
public class MajestikCodeVisitorTest {
	MajestikCodeVisitor visitor;
	CodeBuilder mockCodeBuilder;

	@Before
	public void setUp() {
		mockCodeBuilder = mock(CodeBuilder.class);
		visitor = new MajestikCodeVisitor(mockCodeBuilder);
	}

	@Test
	public void testVisitString() {
		// Test input
		MajestikParser.StringContext mockContext = mock(MajestikParser.StringContext.class);
		when(mockContext.getText()).thenReturn("\"example\"");

		// Execute
		visitor.visitString(mockContext);

		// Verify
		verify(mockCodeBuilder).invokedynamic((InvokeDynamicEntry) any());

		// Additional checks can be added here to confirm the correct parameters are
		// passed to invokedynamic
	}

	@Test
	public void testVisitBlock_statement() {
		// Test input
		MajestikParser.BlockContext mockContext = mock(MajestikParser.BlockContext.class);

		// Execute
		visitor.visitBlock(mockContext);

		// This method primarily logs output, so you might want to check the logs if
		// necessary
		// For example, using a logging framework that supports capturing logs in tests
	}

	@Test
	public void testVisitInvoke() {
		// Test input
		MajestikParser.InvokeContext mockContext = mock(MajestikParser.InvokeContext.class);
		when(mockContext.name.getText()).thenReturn("sw");

		// Execute
		visitor.visitInvoke(mockContext);

		// Verify
		verify(mockCodeBuilder, times(2)).invokedynamic((InvokeDynamicEntry) any());

		// Additional checks can be added here to confirm the correct parameters are
		// passed to invokedynamic
	}
}

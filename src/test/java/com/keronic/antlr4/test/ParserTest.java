package com.keronic.antlr4.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.keronic.antlr4.MajestikLexer;
import com.keronic.antlr4.MajestikParser;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CommonToken;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ListTokenSource;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;

public class ParserTest {
	ANTLRErrorListener errorListener = new BaseErrorListener() {

		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
				String msg, RecognitionException e) {
			fail();
		}
	};

	private MajestikParser getParser(List<Token> tokens) {
		var parser = new MajestikParser(new CommonTokenStream(new ListTokenSource(tokens)));
		parser.addErrorListener(errorListener);
		return parser;
	}

	@Test
	public void testInvokeNoArgs() {
		var parser = this.getParser(Arrays.asList(
				new CommonToken(MajestikLexer.ID, "proc"),
				new CommonToken(MajestikLexer.LEFT_RBRACKET, "("),
				new CommonToken(MajestikLexer.RIGHT_RBRACKET, ")")));
		var invoke = parser.invoke();
		System.out.format("%s \n", invoke.toStringTree());

		assertEquals("([] proc ( ))", invoke.toStringTree());
		assertEquals(null, invoke.argss);
	}

	@Test
	public void testAssignString() {
		var parser = this.getParser(Arrays.asList(
				new CommonToken(MajestikLexer.ID, "var"),
				new CommonToken(MajestikLexer.ASSIGN, "<<"),
				new CommonToken(MajestikLexer.STRING, "\"value\"")));
		var func = parser.assign();
		assertEquals("([] var << ([75] \"value\"))", func.toStringTree());
	}

	@Test
	public void testBlock() {
		var parser = this.getParser(Arrays.asList(
				new CommonToken(MajestikLexer.BLOCK, "_block"),
				new CommonToken(MajestikLexer.ENDBLOCK, "_endblock")));
		var block = parser.block();
		assertEquals("([] _block _endblock)", block.toStringTree());
	}
}

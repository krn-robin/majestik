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
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;

public class ParserTest {
	ANTLRErrorListener errorListener = new BaseErrorListener() {

		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
				String msg, RecognitionException e) {
			fail();
		}
	};

	private String getTreeString(String[] names, RuleContext rc) {
		var buf = new StringBuilder();
		buf.append(String.format("%s%s", '(', names[rc.getRuleIndex()]));

		var subbuf = new StringBuilder();
		for (int i = 0; i < rc.getChildCount(); i++) {
			var t = rc.getChild(i);
			if (t instanceof RuleContext)
				subbuf.append(this.getTreeString(names, (RuleContext) t));
		}

		if (!subbuf.isEmpty())
			buf.append(String.format(" %s", subbuf.toString()));
		buf.append(")");

		return buf.toString();
	}

	private MajestikParser getParser(List<Token> tokens) {
		var parser = new MajestikParser(new CommonTokenStream(new ListTokenSource(tokens)));
		parser.addErrorListener(errorListener);
		return parser;
	}

	@Test
	public void testInvokeNoArgs() {
		var parser = this.getParser(Arrays.asList(
				new CommonToken(MajestikLexer.ID, "function"),
				new CommonToken(MajestikLexer.LEFT_RBRACKET, "("),
				new CommonToken(MajestikLexer.RIGHT_RBRACKET, ")")));
		var func = parser.prog();
		assertEquals("function()EOF", func.getText());
		// assertEquals(null, func.params);
	}

	@Test
	public void testBlock() {
		var parser = this.getParser(Arrays.asList(
				new CommonToken(MajestikLexer.BLOCK, "_block"),
				// new CommonToken(MajestikLexer.ID, "function"),
				// new CommonToken(MajestikLexer.LEFT_RBRACKET, "("),
				// new CommonToken(MajestikLexer.RIGHT_RBRACKET, ")"),

				new CommonToken(MajestikLexer.ENDBLOCK, "_endblock")));
		var func = parser.prog();
		if (true)
			return;
		assertEquals("(block)", this.getTreeString(parser.getRuleNames(), func.getRuleContext()));
	}
}

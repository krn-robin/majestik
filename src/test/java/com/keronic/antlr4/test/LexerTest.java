package com.keronic.antlr4.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.junit.Test;

import com.keronic.antlr4.MajestikLexer;

public class LexerTest {
	ANTLRErrorListener errorListener = new BaseErrorListener() {

		@Override
		public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine,
				String msg, RecognitionException e) {
			fail(String.format("%i %i %s %n", line, charPositionInLine, msg, offendingSymbol));
		}
	};

	private List<Token> getTokensFromText(String txt) throws IOException {
		var lex = new MajestikLexer(CharStreams.fromString(txt));
		lex.addErrorListener(errorListener);
		var str = new CommonTokenStream(lex);
		str.fill();
		var tokens = str.getTokens();
		tokens.removeLast(); // EOF
		return tokens;
	}

	@Test
	public void testBlock() throws IOException {
		var tokens = this.getTokensFromText("_block _endblock");
		assertEquals(2, tokens.size());
		assertEquals(MajestikLexer.BLOCK, tokens.get(0).getType());
		assertEquals(MajestikLexer.ENDBLOCK, tokens.get(1).getType());
	}

	@Test
	public void testEmptyStringDoubleQuote() throws IOException {
		var tokens = this.getTokensFromText("\"\"");
		assertEquals(1, tokens.size());
		assertEquals(MajestikLexer.STRING, tokens.get(0).getType());
	}

	@Test
	public void testEmptyStringSingleQuote() throws IOException {
		var tokens = this.getTokensFromText("''");
		assertEquals(1, tokens.size());
		assertEquals(MajestikLexer.STRING, tokens.get(0).getType());
	}
}

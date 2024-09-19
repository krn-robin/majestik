package com.keronic.antlr4;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

public class LexerTest {
  ANTLRErrorListener errorListener =
      new BaseErrorListener() {

		@Override
        public void syntaxError(
            Recognizer<?, ?> recognizer,
            Object offendingSymbol,
            int line,
            int charPositionInLine,
            String msg,
            RecognitionException e) {
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

	private void compareTokens(int[] expected, List<Token> actual) {
		assertEquals(expected.length, actual.size());
        IntStream.range(0, expected.length)
                 .forEach(n -> assertEquals(expected[n], actual.get(n).getType()));
	}

	@Test
	public void testAssignString() throws IOException {
    final var expected = new int[] {MajestikLexer.VAR, MajestikLexer.ASSIGN, MajestikLexer.STRING};
		var tokens = this.getTokensFromText("var << \"value\"");

		this.compareTokens(expected, tokens);
	}

	@Test
	public void testBlock() throws IOException {
    final var expected = new int[] {MajestikLexer.BLOCK, MajestikLexer.ENDBLOCK};
		var tokens = this.getTokensFromText("_block _endblock");

		this.compareTokens(expected, tokens);
	}

	@Test
	public void testCaseInsensitive() throws IOException {
    final var expected =
        new int[] {MajestikLexer.ENDBLOCK, MajestikLexer.ENDBLOCK, MajestikLexer.ENDBLOCK};
		var tokens = this.getTokensFromText("_EndBlock _ENDBLOCK _eNDBLOCk");

		this.compareTokens(expected, tokens);
	}

	@Test
	public void testEmptyStringDoubleQuote() throws IOException {
    final var expected = new int[] {MajestikLexer.STRING};
		var tokens = this.getTokensFromText("\"\"");

		this.compareTokens(expected, tokens);
	}

	@Test
	public void testEmptyStringSingleQuote() throws IOException {
    final var expected = new int[] {MajestikLexer.STRING};
		var tokens = this.getTokensFromText("''");

		this.compareTokens(expected, tokens);
	}

	@Test
	public void testNumberLong() throws IOException {
    final var expected = new int[] {MajestikLexer.NUMBER};
		var tokens = this.getTokensFromText("54321");

		this.compareTokens(expected, tokens);
	}

	@Test
	public void testNumberDouble() throws IOException {
    final var expected = new int[] {MajestikLexer.NUMBER};
		var tokens = this.getTokensFromText("5.4321");

		this.compareTokens(expected, tokens);
	}

	@Test
	public void testVariable() throws IOException {
    final var expected = new int[] {MajestikLexer.VAR};
		var tokens = this.getTokensFromText("var");

		this.compareTokens(expected, tokens);
	}
}

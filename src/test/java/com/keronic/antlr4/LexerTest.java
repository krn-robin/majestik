package com.keronic.antlr4;

import module java.base;

import static org.junit.jupiter.api.Assertions.*;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.junit.jupiter.api.Test;

class LexerTest {
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
          fail(String.format("%s (%d:%d)", msg, line, charPositionInLine));
        }
      };

  @Test
  public void testBlockMultiLine() {
    final var expected =
        new int[] {MajestikLexer.BLOCK, MajestikLexer.CRLF, MajestikLexer.ENDBLOCK};
    var tokens = this.getTokensFromText(String.format("_block%n_endblock"));

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testIf() {
    final var expected =
        new int[] {
          MajestikLexer.IF,
          MajestikLexer.THEN,
          MajestikLexer.ELIF,
          MajestikLexer.ELSE,
          MajestikLexer.ENDIF
        };
    var tokens = this.getTokensFromText("_if _then _elif _else _endif");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testBoolean() {
    final var expected = new int[] {MajestikLexer.FALSE, MajestikLexer.TRUE};
    var tokens = this.getTokensFromText("_false _true");

    this.compareTokens(expected, tokens);
  }

  @Test
  void testAssignString() {
    final var expected = new int[] {MajestikLexer.VAR, MajestikLexer.ASSIGN, MajestikLexer.STRING};
    var tokens = this.getTokensFromText("var << \"value\"");

    this.compareTokens(expected, tokens);
  }

  @Test
  void testBlock() {
    final var expected = new int[] {MajestikLexer.BLOCK, MajestikLexer.ENDBLOCK};
    var tokens = this.getTokensFromText("_block _endblock");

    this.compareTokens(expected, tokens);
  }

  @Test
  void testCaseInsensitive() {
    final var expected =
        new int[] {MajestikLexer.ENDBLOCK, MajestikLexer.ENDBLOCK, MajestikLexer.ENDBLOCK};
    var tokens = this.getTokensFromText("_EndBlock _ENDBLOCK _eNDBLOCk");

    this.compareTokens(expected, tokens);
  }

  @Test
  void testEmptyStringDoubleQuote() {
    final var expected = new int[] {MajestikLexer.STRING};
    var tokens = this.getTokensFromText("\"\"");

    this.compareTokens(expected, tokens);
  }

  @Test
  void testEmptyStringSingleQuote() {
    final var expected = new int[] {MajestikLexer.STRING};
    var tokens = this.getTokensFromText("''");

    this.compareTokens(expected, tokens);
  }

  @Test
  void testNumberDouble() {
    final var expected = new int[] {MajestikLexer.NUMBER};
    var tokens = this.getTokensFromText("5.4321");

    this.compareTokens(expected, tokens);
  }

  @Test
  void testNumberLong() {
    final var expected = new int[] {MajestikLexer.NUMBER};
    var tokens = this.getTokensFromText("54321");

    this.compareTokens(expected, tokens);
  }

  @Test
  void testVariable() {
    final var expected = new int[] {MajestikLexer.VAR};
    var tokens = this.getTokensFromText("var");

    this.compareTokens(expected, tokens);
  }

  private List<Token> getTokensFromText(String txt) {
    var lex = new MajestikLexer(CharStreams.fromString(txt));
    lex.addErrorListener(errorListener);
    var str = new CommonTokenStream(lex);
    str.fill();
    var tokens = str.getTokens();
    tokens.removeIf(t -> t.getChannel() == MajestikLexer.HIDDEN);
    tokens.removeLast(); // EOF
    return tokens;
  }

  private void compareTokens(int[] expected, List<Token> actual) {
    var symbolicnames =
        actual.stream()
            .map(t -> MajestikLexer.VOCABULARY.getSymbolicName(t.getType()))
            .collect(Collectors.toList());
    var assertmsg = String.join(",", symbolicnames);
    assertEquals(expected.length, actual.size(), assertmsg);
    IntStream.range(0, expected.length)
        .forEach(n -> assertEquals(expected[n], actual.get(n).getType()));
  }
}

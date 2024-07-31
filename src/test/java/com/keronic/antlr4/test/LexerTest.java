package com.keronic.antlr4.test;

import static com.keronic.antlr4.MajestikLexer.*;
import static org.junit.Assert.*;

import com.keronic.antlr4.MajestikLexer;
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
import org.junit.Test;

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
    final var expected = new int[] {VAR, ASSIGN, STRING};
		var tokens = this.getTokensFromText("var << \"value\"");

		this.compareTokens(expected, tokens);
	}

	@Test
	public void testBlock() throws IOException {
    final var expected = new int[] {BLOCK, ENDBLOCK};
		var tokens = this.getTokensFromText("_block _endblock");

		this.compareTokens(expected, tokens);
	}

	@Test
	public void testCaseInsensitive() throws IOException {
    final var expected = new int[] {ENDBLOCK, ENDBLOCK, ENDBLOCK};
		var tokens = this.getTokensFromText("_EndBlock _ENDBLOCK _eNDBLOCk");

		this.compareTokens(expected, tokens);
	}

  @Test
  public void testCatch() throws IOException {
    final var expected = new int[] {CATCH, THROW, ENDCATCH};
    var tokens = this.getTokensFromText("_catch _throw _endcatch");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testClass() throws IOException {
    final var expected = new int[] {CLASS};
    var tokens = this.getTokensFromText("_class");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testCompare() throws IOException {
    final var expected = new int[] {CF};
    var tokens = this.getTokensFromText("_cf");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testDeclaration() throws IOException {
    final var expected = new int[] {CONSTANT, DYNAMIC, GLOBAL, IMPORT, LOCAL};
    var tokens = this.getTokensFromText("_constant _dynamic _global _import _local");

    this.compareTokens(expected, tokens);
  }

	@Test
	public void testEmptyStringDoubleQuote() throws IOException {
    final var expected = new int[] {STRING};
		var tokens = this.getTokensFromText("\"\"");

		this.compareTokens(expected, tokens);
	}

	@Test
	public void testEmptyStringSingleQuote() throws IOException {
    final var expected = new int[] {STRING};
		var tokens = this.getTokensFromText("''");

		this.compareTokens(expected, tokens);
	}

	@Test
  public void testFor() throws IOException {
    final var expected = new int[] {FOR, OVER, CONTINUE, FINALLY};
    var tokens = this.getTokensFromText("_for _over _continue _finally");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testGather() throws IOException {
    final var expected = new int[] {ALLRESULTS, GATHER, SCATTER};
    var tokens = this.getTokensFromText("_allresults _gather _scatter");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testHandling() throws IOException {
    final var expected = new int[] {HANDLING, DEFAULT, WITH};
    var tokens = this.getTokensFromText("_handling _default _with");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testIf() throws IOException {
    final var expected = new int[] {IF, THEN, ELIF, ELSE, ENDIF};
    var tokens = this.getTokensFromText("_if _then _elif _else _endif");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testLoop() throws IOException {
    final var expected = new int[] {WHILE, LOOP, LEAVE, LOOPBODY, ENDLOOP};
    var tokens = this.getTokensFromText("_while _loop _leave _loopbody _endloop");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testMethod() throws IOException {
    final var expected = new int[] {ABSTRACT, PRIVATE, METHOD, ENDMETHOD};
    var tokens = this.getTokensFromText("_abstract _private _method _endmethod");

		this.compareTokens(expected, tokens);
	}

	@Test
	public void testNumberDouble() throws IOException {
    final var expected = new int[] {NUMBER};
		var tokens = this.getTokensFromText("5.4321");

		this.compareTokens(expected, tokens);
	}

  @Test
  public void testNumberLong() throws IOException {
    final var expected = new int[] {NUMBER};
    var tokens = this.getTokensFromText("54321");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testOptional() throws IOException {
    final var expected = new int[] {OPTIONAL};
    var tokens = this.getTokensFromText("_optional");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testPackage() throws IOException {
    final var expected = new int[] {PACKAGE};
    var tokens = this.getTokensFromText("_package");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testPrimitive() throws IOException {
    final var expected = new int[] {PRIMITIVE};
    var tokens = this.getTokensFromText("_primitive");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testProc() throws IOException {
    final var expected = new int[] {ITER, PROC, RETURN, ENDPROC};
    var tokens = this.getTokensFromText("_iter _proc _return _endproc");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testProtection() throws IOException {
    final var expected = new int[] {PROTECT, LOCKING, PROTECTION, ENDPROTECT};
    var tokens = this.getTokensFromText("_protect _locking _protection _endprotect");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testThisThread() throws IOException {
    final var expected = new int[] {THISTHREAD};
    var tokens = this.getTokensFromText("_thisthread");

    this.compareTokens(expected, tokens);
  }

  @Test
  public void testTry() throws IOException {
    final var expected = new int[] {TRY, WHEN, ENDTRY};
    var tokens = this.getTokensFromText("_try _when _endtry");

    this.compareTokens(expected, tokens);
  }

	@Test
	public void testVariable() throws IOException {
    final var expected = new int[] {VAR};
		var tokens = this.getTokensFromText("var");

		this.compareTokens(expected, tokens);
	}
}

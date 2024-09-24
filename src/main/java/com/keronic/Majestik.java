/** */
package com.keronic;

import module java.base;

import com.keronic.antlr4.MajestikBaseVisitor;
import com.keronic.antlr4.MajestikLexer;
import com.keronic.antlr4.MajestikParser;
import com.keronic.antlr4.MajestikParser.ProgContext;
import com.keronic.majestik.MajestikRuntimeException;
import java.lang.System.Logger.Level;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/** */
public class Majestik {
  private static final System.Logger LOGGER =
      System.getLogger(MethodHandles.lookup().lookupClass().getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
    LOGGER.log(Level.INFO, () -> "Majestik v0.0");

		try {
      // FIXME: force load of stub sw:write proc
			@SuppressWarnings("unused")
      var wp = Class.forName("com.keronic.majestik.runtime.WriteProcTemp");
		} catch (Exception e) {
      throw new MajestikRuntimeException(e);
		}

    if (args.length == 0) LOGGER.log(Level.ERROR, () -> "Usage: majestik file.magik");
		else
			try {
        LOGGER.log(Level.INFO, () -> String.format("Reading file: %s", args[0]));
				CharStream in = CharStreams.fromFileName(args[0]);
				MajestikLexer lexer = new MajestikLexer(in);
				MajestikParser parser = new MajestikParser(new CommonTokenStream(lexer));
				ProgContext tree = parser.prog(); // parse
        MajestikBaseVisitor<Void> mbv =
            new MajestikBaseVisitor<Void>(); // Yield unrecognized tokens early
				mbv.visit(tree);

        LOGGER.log(Level.INFO, () -> String.format("Compiling..."));
				var baseName = PathUtils.getBaseName(args[0]);
        Files.createDirectories(Path.of("majestik"));

				var newFileName = "majestik/%s.class".formatted(baseName); // TODO: move to pathutils
        LOGGER.log(Level.INFO, () -> String.format("Writing to classfile: %s%n", newFileName));
        ClassFile.of()
            .buildTo(
                Path.of(newFileName),
						ClassDesc.of("majestik.%s".formatted(baseName)),
                classBuilder ->
                    classBuilder
                        .withMethodBody(
                            ConstantDescs.INIT_NAME,
                            ConstantDescs.MTD_void,
                            ClassFile.ACC_PUBLIC,
                            codeBuilder ->
                                codeBuilder
                                    .aload(0)
                                    .invokespecial(
                                        ConstantDescs.CD_Object,
                                        ConstantDescs.INIT_NAME,
														ConstantDescs.MTD_void)
												.return_())
                        .withMethodBody(
                            ConstantDescs.CLASS_INIT_NAME,
                            ConstantDescs.MTD_void,
                            ClassFile.ACC_STATIC,
                            codeBuilder -> {
											MajestikCodeVisitor mcv = new MajestikCodeVisitor(codeBuilder);
											mcv.visit(tree);
											codeBuilder.return_();
										}));
				var cl = new MajestikClassLoader();

        LOGGER.log(
            Level.INFO,
            () -> String.format("LOG: Loading compiled class: %s", Path.of(newFileName)));
				cl.loadClass("majestik.%s".formatted(baseName)).getDeclaredConstructor().newInstance();
			} catch (Exception e) {
        throw new MajestikRuntimeException(e);
			}
	}
}

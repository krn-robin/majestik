/** */
package com.keronic;

import com.keronic.antlr4.MajestikBaseVisitor;
import com.keronic.antlr4.MajestikLexer;
import com.keronic.antlr4.MajestikParser;
import com.keronic.antlr4.MajestikParser.ProgContext;
import com.keronic.majestik.MajestikRuntimeException;
import java.io.File;
import java.lang.classfile.ClassFile;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.nio.file.Path;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/** */
public class Majestik {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Majestik v0.0");

		try {
			@SuppressWarnings("unused")
      var wp =
          Class.forName(
              "com.keronic.majestik.runtime.WriteProcTemp"); // FIXME: force load of stub sw:write
      // proc
		} catch (Exception e) {
      throw new MajestikRuntimeException(e);
		}

		if (args.length == 0)
			System.out.println("Usage: majestik file.magik");
		else
			try {
				System.out.println(String.format("LOG: Reading file: %s", args[0]));
				CharStream in = CharStreams.fromFileName(args[0]);
				MajestikLexer lexer = new MajestikLexer(in);
				MajestikParser parser = new MajestikParser(new CommonTokenStream(lexer));
				ProgContext tree = parser.prog(); // parse
				MajestikBaseVisitor<Void> mbv = new MajestikBaseVisitor<Void>(); // Yield unrecognized tokens early
				mbv.visit(tree);

				System.out.println("LOG: Compiling...");
				var baseName = PathUtils.getBaseName(args[0]);
				var dir = new File("majestik");
				if (!dir.exists())
					dir.mkdir(); // TODO: move to pathutils

				var newFileName = "majestik/%s.class".formatted(baseName); // TODO: move to pathutils
				System.out.format("Writing to classfile: %s\n", newFileName);
				ClassFile.of().buildTo(Path.of(newFileName),
						ClassDesc.of("majestik.%s".formatted(baseName)),
						classBuilder -> classBuilder
								.withMethodBody(ConstantDescs.INIT_NAME, ConstantDescs.MTD_void, ClassFile.ACC_PUBLIC,
										codeBuilder -> codeBuilder.aload(0)
												.invokespecial(ConstantDescs.CD_Object, ConstantDescs.INIT_NAME,
														ConstantDescs.MTD_void)
												.return_())
								.withMethodBody(ConstantDescs.CLASS_INIT_NAME, ConstantDescs.MTD_void,
										ClassFile.ACC_STATIC, codeBuilder -> {
											MajestikCodeVisitor mcv = new MajestikCodeVisitor(codeBuilder);
											mcv.visit(tree);
											codeBuilder.return_();
										}));
				var cl = new MajestikClassLoader();

				System.out.println(String.format("LOG: Loading compiled class: %s",
						Path.of(newFileName)));
				cl.loadClass("majestik.%s".formatted(baseName)).getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}

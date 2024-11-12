/** */
package com.keronic;

import module java.base;

import com.keronic.majestik.MajestikRuntimeException;
import nl.ramsolutions.sw.MagikToolsProperties;
import nl.ramsolutions.sw.magik.MagikFile;
import java.lang.System.Logger.Level;

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
        var mf = new MagikFile(MagikToolsProperties.DEFAULT_PROPERTIES, Path.of(args[0]));

        LOGGER.log(Level.INFO, () -> String.format("Compiling..."));
				var baseName = PathUtils.getBaseName(args[0]);
        Files.createDirectories(Path.of("majestik"));

				var newFileName = "majestik/%s.class".formatted(baseName); // TODO: move to pathutils
        LOGGER.log(Level.INFO, () -> String.format("Writing to classfile: %s", newFileName));
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
                              mcv.scanFile(mf);
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

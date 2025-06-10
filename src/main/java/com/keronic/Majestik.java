package com.keronic;

import module java.base;

import static java.lang.classfile.ClassFile.ACC_PUBLIC;
import static java.lang.classfile.ClassFile.ACC_STATIC;
import static java.lang.constant.ConstantDescs.CD_Object;
import static java.lang.constant.ConstantDescs.CLASS_INIT_NAME;
import static java.lang.constant.ConstantDescs.INIT_NAME;
import static java.lang.constant.ConstantDescs.MTD_void;
import module java.base;

import com.keronic.majestik.MajestikRuntimeException;
import com.keronic.majestik.ast.CompilationContext;
import com.keronic.majestik.ast.MajestikCodeVisitor;
import java.lang.System.Logger.Level;
import nl.ramsolutions.sw.MagikToolsProperties;
import nl.ramsolutions.sw.magik.MagikFile;

/** */
public class Majestik {
  private static final System.Logger LOGGER =
      System.getLogger(MethodHandles.lookup().lookupClass().getName());

  static void buildClass(MagikFile mf, ClassBuilder cb) {
    cb.withMethodBody(INIT_NAME, MTD_void, ACC_PUBLIC, Majestik::buildEmptyInitMethod)
        .withMethodBody(CLASS_INIT_NAME, MTD_void, ACC_STATIC,
            cb2 -> buildClassInitMethod(mf, cb2));
  }

  static void buildEmptyInitMethod(CodeBuilder cb) {
    cb.aload(0).invokespecial(CD_Object, INIT_NAME, MTD_void).return_();
  }

  static void buildClassInitMethod(MagikFile mf, CodeBuilder cb) {
    MajestikCodeVisitor mcv = new MajestikCodeVisitor();
    mcv.scanFile(mf).compileInto(new CompilationContext(cb));
    cb.return_();
  }

  /**
   * The main entry point of the Majestik test application.
   *
   * @param args command-line arguments; expects a single Magik file to compile.
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

    if (args.length == 0)
      LOGGER.log(Level.ERROR, () -> "Usage: majestik file.magik");
    else
      try {
        LOGGER.log(Level.INFO, () -> String.format("Reading file: %s", args[0]));
        var mf = new MagikFile(MagikToolsProperties.DEFAULT_PROPERTIES, Path.of(args[0]));

        LOGGER.log(Level.INFO, () -> String.format("Compiling..."));

        var baseName = PathUtils.getBaseName(Paths.get(args[0]).getFileName().toString());
        Files.createDirectories(Path.of("majestik"));

        var newFileName = "majestik/%s.class".formatted(baseName); // TODO: move to pathutils
        LOGGER.log(Level.INFO, () -> String.format("Writing to classfile: %s", newFileName));
        ClassFile.of().buildTo(Path.of(newFileName),
            ClassDesc.of("majestik.%s".formatted(baseName)), cb -> buildClass(mf, cb));

        LOGGER.log(Level.INFO,
            () -> String.format("LOG: Loading compiled class: %s", Path.of(newFileName)));
        var cl = new MajestikClassLoader();
        cl.loadClass("majestik.%s".formatted(baseName)).getDeclaredConstructor().newInstance();
      } catch (Exception e) {
        throw new MajestikRuntimeException(e);
      }
  }
}

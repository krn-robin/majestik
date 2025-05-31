package com.keronic;

import java.io.File; // Keep for File.separatorChar in findClass potentially, or loadClassData if it used it
import java.io.IOException;
// import java.io.InputStream; // Not used if loadClassData body is gone
// import java.nio.file.Files; // Not used if loadClassData body is gone
// import java.nio.file.Path; // Not used if loadClassData body is gone
// import java.util.function.Function; // For REMAP_FUNCTION
// import java.lang.constant.ClassDesc; // For REMAP_FUNCTION

/** */
public class MajestikClassLoader extends ClassLoader {

  /*
  static final Function<ClassDesc, ClassDesc> REMAP_FUNCTION =
      cd ->
          ClassDesc.of(
              cd.packageName().replace("com.gesmallworld.magik", "com.keronic.majestik"),
              cd.displayName());
  */

  public MajestikClassLoader() {
    super("Majestik", getSystemClassLoader());
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    if (name.startsWith("magik") || name.startsWith("majestik")) {
      try {
        var cd = this.loadClassData(name); // This call will now do very little
        // Since loadClassData is expected to return byte[], and now it won't,
        // this defineClass call will fail if cd is null or not a byte array.
        // For the purpose of this FQN test, we might need to make loadClassData return null
        // and handle that in findClass, or let it throw an IOException that findClass catches.
        // The subtask implies loadClassData should still declare `throws IOException`.
        if (cd == null) { // Or check if it's the dummy byte array if we made one
             throw new ClassNotFoundException(name + " (Majestik: loadClassData returned null)");
        }
        return super.defineClass(name, cd, 0, cd.length);
      } catch (IOException e) {
        // If loadClassData throws an IOException (e.g. "Not implemented for test"),
        // this catch block will handle it.
        // Depending on the test, we might want loadClassData to throw or return null.
        // For a pure FQN resolution test, loadClassData just needs to compile.
        // The original code had `return super.findClass(name);` here.
        throw new ClassNotFoundException(name, e);
      }
    }
    return super.findClass(name);
  }

  private byte[] loadClassData(String className) throws IOException {
    /*
    var classFileName = className.replace('.', File.separatorChar) + ".class";
    byte[] classBytes;
    try {
      Path path = Path.of(classFileName);
      if (!Files.exists(path)) {
        InputStream is = getResourceAsStream(classFileName);
        if (is == null) {
          throw new IOException("Class file not found: " + classFileName + " via direct path or resource stream.");
        }
        classBytes = is.readAllBytes();
      } else {
        classBytes = Files.readAllBytes(path);
      }
    } catch (IOException e) {
      throw new IOException("Failed to read class file: " + classFileName, e);
    }

    ClassModel classModel;
    try {
      classModel = ClassFile.of().parse(classBytes);
    } catch (Exception e) {
      throw new IOException("Failed to parse class file: " + classFileName, e);
    }

    ClassDesc originalThisClassDesc = classModel.thisClass().asSymbol();
    ClassDesc remappedThisClassDesc = REMAP_FUNCTION.apply(originalThisClassDesc);

    return ClassFile.of().build(remappedThisClassDesc, classBuilder -> {
        // Apply original model's version and flags (excluding ThisClass name)
        if (classModel.majorVersion() > 0) {
            classBuilder.withVersion(classModel.majorVersion(), classModel.minorVersion());
        }
        classBuilder.withFlags(classModel.flags().flagsMask());

        ConstantPoolBuilder cp = classBuilder.constantPool();

        for (ClassElement ce : classModel) {
            if (ce instanceof ClassFileVersion) {
                continue;
            }
            else if (ce instanceof InnerClassesAttribute ica) {
                List<java.lang.classfile.attribute.InnerClassesAttribute.InnerClassInfo> remappedInnerClasses = new ArrayList<>();
                // ConstantPoolBuilder cp is already available from ClassBuilder

                for (java.lang.classfile.attribute.InnerClassesAttribute.InnerClassInfo entry : ica.classes()) {
                    ClassDesc innerClsSym = entry.innerClass().asSymbol();
                    ClassDesc remappedInnerClsSym = REMAP_FUNCTION.apply(innerClsSym);

                    Optional<java.lang.classfile.constantpool.ClassEntry> remappedOuterEntryOpt = entry.outerClass()
                        .map(java.lang.classfile.constantpool.ClassEntry::asSymbol)
                        .map(REMAP_FUNCTION)
                        .map(cp::classEntry);

                    Optional<java.lang.classfile.constantpool.Utf8Entry> innerNameOpt = entry.innerName()
                        .map(nameUtf8Entry -> cp.utf8Entry(nameUtf8Entry.stringValue()));

                    remappedInnerClasses.add(
                        java.lang.classfile.attribute.InnerClassesAttribute.InnerClassInfo.of(
                            cp.classEntry(remappedInnerClsSym),
                            remappedOuterEntryOpt,
                            innerNameOpt,
                            entry.accessFlags()
                        )
                    );
                }

                if (!remappedInnerClasses.isEmpty()) {
                    classBuilder.with(java.lang.classfile.attribute.InnerClassesAttribute.of(remappedInnerClasses));
                }
            }
        }
    });
    */
    java.lang.classfile.ClassFile testCf = null;
    // To make this method satisfy its byte[] return type for the findClass method,
    // and to avoid NullPointerException there if findClass doesn't handle null well:
    // Option 1: Return a dummy byte array (e.g., new byte[0])
    // Option 2: Let it throw an IOException as it declares.
    // Option 3: Return null and ensure findClass handles it.
    // The subtask is about FQN resolution, so a compile error or a specific runtime error
    // if ClassFile is not found is the target. If it compiles, the FQN is resolved.
    // To ensure it compiles and findClass doesn't immediately break due to return type:
    // throw new IOException("Ultra-minimal test: loadClassData not implemented.");
    // Or, for a quieter test that still allows findClass to proceed (and likely fail later):
    return null; // findClass needs to handle this, or it will NPE.
                 // The provided findClass has a try-catch for IOException.
                 // If we return null, then `cd.length` will NPE.
                 // Let's make it throw to be caught by findClass.
    // throw new IOException("Ultra-minimal test: loadClassData intentionally not returning class bytes.");

  }

  /*
  private static void transformCode(CodeBuilder codeBuilder, CodeModel codeModel, Function<ClassDesc, ClassDesc> remapFunction) {
    for (CodeElement element : codeModel) {
        if (element instanceof InvokeInstruction instr) {
            Opcode opcode = instr.opcode();
            ClassDesc owner = instr.owner().asSymbol();
            ClassDesc remappedOwner = remapFunction.apply(owner);
            MethodTypeDesc type = instr.typeSymbol();
            MethodTypeDesc remappedType = MethodTypeDesc.of(
                    remapClassDescRecursively(type.returnType(), remapFunction),
                    type.parameterList().stream()
                            .map(p -> remapClassDescRecursively(p, remapFunction))
                            .toArray(ClassDesc[]::new));
            String methodName = instr.name().stringValue();
            boolean isInterface = instr.isInterface();

            switch (opcode) {
                case INVOKEVIRTUAL -> codeBuilder.invokevirtual(remappedOwner, methodName, remappedType);
                case INVOKESPECIAL -> codeBuilder.invokespecial(remappedOwner, methodName, remappedType, isInterface);
                case INVOKESTATIC -> codeBuilder.invokestatic(remappedOwner, methodName, remappedType, isInterface);
                case INVOKEINTERFACE -> codeBuilder.invokeinterface(remappedOwner, methodName, remappedType);
                case INVOKEDYNAMIC -> codeBuilder.with(instr);
                default -> codeBuilder.with(instr);
            }
        } else if (element instanceof FieldInstruction instr) {
            Opcode opcode = instr.opcode();
            ClassDesc owner = instr.owner().asSymbol();
            ClassDesc remappedOwner = remapFunction.apply(owner);
            ClassDesc fieldType = instr.typeSymbol();
            ClassDesc remappedFieldType = remapClassDescRecursively(fieldType, remapFunction);
            String fieldName = instr.name().stringValue();

            switch (opcode) {
                case GETSTATIC -> codeBuilder.getstatic(remappedOwner, fieldName, remappedFieldType);
                case PUTSTATIC -> codeBuilder.putstatic(remappedOwner, fieldName, remappedFieldType);
                case GETFIELD -> codeBuilder.getfield(remappedOwner, fieldName, remappedFieldType);
                case PUTFIELD -> codeBuilder.putfield(remappedOwner, fieldName, remappedFieldType);
                default -> codeBuilder.with(instr);
            }
        } else if (element instanceof TypeCheckInstruction instr) {
            Opcode opcode = instr.opcode();
            ClassDesc originalType = instr.type().asSymbol();
            ClassDesc remappedType = remapFunction.apply(originalType);
            if (opcode == Opcode.INSTANCEOF) {
                codeBuilder.instanceofInstruction(remappedType);
            } else if (opcode == Opcode.CHECKCAST) {
                codeBuilder.checkcast(remappedType);
            } else {
                 codeBuilder.with(instr);
            }
        } else if (element instanceof NewObjectInstruction instr) {
            ClassDesc originalType = instr.className().asSymbol();
            ClassDesc remappedType = remapFunction.apply(originalType);
            codeBuilder.newObject(remappedType);
        } else if (element instanceof NewReferenceArrayInstruction instr) {
            ClassDesc componentType = instr.componentType().asSymbol();
            ClassDesc remappedComponentType = remapFunction.apply(componentType);
            codeBuilder.anewarray(remappedComponentType);
        } else if (element instanceof NewMultiArrayInstruction instr) {
             ClassDesc originalArrayType = instr.arrayType().asSymbol();
             ClassDesc remappedArrayType = remapClassDescRecursively(originalArrayType, remapFunction);
             codeBuilder.multianewarray(remappedArrayType, instr.dimensions());
        } else if (element instanceof ConstantInstruction instr) {
            java.lang.constant.ConstantDesc constDesc = instr.constantValue();
            if (constDesc instanceof ClassDesc cdConst) {
                codeBuilder.ldc(remapClassDescRecursively(cdConst, remapFunction));
            } else if (constDesc instanceof MethodTypeDesc mtdConst) {
                MethodTypeDesc remappedMtd = MethodTypeDesc.of(
                    remapClassDescRecursively(mtdConst.returnType(), remapFunction),
                    mtdConst.parameterList().stream()
                            .map(p -> remapClassDescRecursively(p, remapFunction))
                            .toArray(ClassDesc[]::new));
                codeBuilder.ldc(remappedMtd);
            }
            else {
                codeBuilder.ldc(constDesc);
            }
        }
        else {
            codeBuilder.with(element);
        }
    }
  }

  private static ClassDesc remapClassDescRecursively(ClassDesc cd, Function<ClassDesc, ClassDesc> remapFunction) {
    if (cd.isPrimitive()) {
      return cd;
    }
    if (cd.isArray()) {
      ClassDesc componentType = cd.componentType();
      ClassDesc remappedComponentType = remapClassDescRecursively(componentType, remapFunction);
      // If the component type itself didn't change, no need to create new array desc
      if (componentType == remappedComponentType) {
        return cd;
      }
      // Rebuild the array structure with the new component type
      int dimensions = 0;
      ClassDesc temp = cd;
      while (temp.isArray()) {
        dimensions++;
        temp = temp.componentType();
      }
      ClassDesc newArrayDesc = remappedComponentType;
      for (int i = 0; i < dimensions; i++) {
        newArrayDesc = newArrayDesc.arrayType();
      }
      return newArrayDesc;
    }
    // Apply the remap function for non-array, non-primitive types
    return remapFunction.apply(cd);
  }
  */
}

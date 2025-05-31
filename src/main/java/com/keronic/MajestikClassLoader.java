package com.keronic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
// import java.io.UncheckedIOException; // Might not be needed if Files.readAllBytes is out of test scope
import java.lang.classfile.AccessFlags;
// import java.lang.classfile.Attribute; // Only if the fallback attribute handler is kept
import java.lang.classfile.ClassBuilder;
import java.lang.classfile.ClassElement;
import java.lang.classfile.ClassFile;
// import java.lang.classfile.ClassFileElement; // Only if the fallback attribute handler is kept
import java.lang.classfile.ClassFileVersion; // For instanceof check to skip
import java.lang.classfile.ClassModel;
import java.lang.classfile.attribute.InnerClassesAttribute;
// import java.lang.classfile.attribute.InnerClassesAttribute.InnerClassInfo; // Do not rely on this, use FQN
import java.lang.classfile.constantpool.ClassEntry;
import java.lang.classfile.constantpool.ConstantPoolBuilder;
import java.lang.classfile.constantpool.Utf8Entry;
import java.lang.constant.ClassDesc;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
// import java.util.stream.Collectors; // Not needed for this isolated test

/** */
public class MajestikClassLoader extends ClassLoader {

  static final Function<ClassDesc, ClassDesc> REMAP_FUNCTION =
      cd ->
          ClassDesc.of(
              cd.packageName().replace("com.gesmallworld.magik", "com.keronic.majestik"),
              cd.displayName());

  public MajestikClassLoader() {
    super("Majestik", getSystemClassLoader());
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    if (name.startsWith("magik") || name.startsWith("majestik")) {
      try {
        var cd = this.loadClassData(name);
        return super.defineClass(name, cd, 0, cd.length);
      } catch (IOException e) {
        return super.findClass(name);
      }
    }
    return super.findClass(name);
  }

  private byte[] loadClassData(String className) throws IOException {
    var classFileName = className.replace('.', File.separatorChar) + ".class";
    byte[] classBytes;
    try {
      Path path = Path.of(classFileName); // This path resolution might need to be more robust in a real classloader
      if (!Files.exists(path)) {
        InputStream is = getResourceAsStream(classFileName); // Use imported InputStream
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

        ConstantPoolBuilder cp = classBuilder.constantPool(); // Get ConstantPoolBuilder once

        for (ClassElement ce : classModel) {
            // Skip ClassFileVersion as it's handled by withVersion or defaults.
            // ThisClass is handled by the first argument to build().
            if (ce instanceof ClassFileVersion) {
                continue;
            }
            // Comment out ALL else if blocks for Superclass, Interfaces, FieldModel, MethodModel,
            // NestHostAttribute, NestMembersAttribute, PermittedSubclassesAttribute,
            // and any generic Attribute<?> fallback.
            /*
            else if (ce instanceof Superclass sc) {
                sc.superclassEntry() // Returns Optional<ClassEntry>
                    .map(java.lang.classfile.constantpool.ClassEntry::asSymbol)
                    .map(REMAP_FUNCTION)
                    .ifPresent(classBuilder::withSuperclass);
            } else if (ce instanceof Interfaces ifs) {
                List<ClassDesc> remappedInterfaceDescs = new ArrayList<>();
                List<java.lang.classfile.constantpool.ClassEntry> interfaceEntries = ifs.interfaces();
                if (interfaceEntries != null) {
                    for (java.lang.classfile.constantpool.ClassEntry interfaceEntry : interfaceEntries) {
                        ClassDesc originalInterfaceDesc = interfaceEntry.asSymbol();
                        remappedInterfaceDescs.add(REMAP_FUNCTION.apply(originalInterfaceDesc));
                    }
                }
                if (!remappedInterfaceDescs.isEmpty()) {
                    classBuilder.withInterfaceSymbols(remappedInterfaceDescs);
                }
            } else if (ce instanceof FieldModel fm) {
                ClassDesc originalFieldTypeDesc = ClassDesc.ofDescriptor(fm.fieldType().stringValue());
                ClassDesc remappedFieldTypeDesc = remapClassDescRecursively(originalFieldTypeDesc, REMAP_FUNCTION);
                classBuilder.withField(fm.fieldName().stringValue(), remappedFieldTypeDesc, fieldBuilder -> {
                    fieldBuilder.withFlags(fm.flags().flagsMask());
                    for (java.lang.classfile.Attribute<?> attr : fm.attributes()) {
                        fieldBuilder.with((java.lang.classfile.FieldElement)attr);
                    }
                });
            } else if (ce instanceof MethodModel mm) {
                java.lang.constant.MethodTypeDesc originalMethodTypeDesc = mm.methodTypeSymbol();
                java.lang.constant.MethodTypeDesc remappedMethodTypeDesc = java.lang.constant.MethodTypeDesc.of(
                        remapClassDescRecursively(originalMethodTypeDesc.returnType(), REMAP_FUNCTION),
                        originalMethodTypeDesc.parameterList().stream()
                                .map(pt -> remapClassDescRecursively(pt, REMAP_FUNCTION))
                                .toArray(java.lang.constant.ClassDesc[]::new)
                );
                classBuilder.withMethod(mm.methodName().stringValue(), remappedMethodTypeDesc, mm.flags().flagsMask(), methodBuilder -> {
                    for (java.lang.classfile.Attribute<?> attr : mm.attributes()) {
                        if (attr instanceof java.lang.classfile.attribute.CodeAttribute) {
                            // Handled by withCode
                        } else if (attr instanceof java.lang.classfile.attribute.ExceptionsAttribute ea) {
                            List<ClassDesc> remappedExceptionDescs = new ArrayList<>();
                            List<java.lang.classfile.constantpool.ClassEntry> exceptionEntries = ea.exceptions();
                            if (exceptionEntries != null) {
                                for (java.lang.classfile.constantpool.ClassEntry entry : exceptionEntries) {
                                    remappedExceptionDescs.add(REMAP_FUNCTION.apply(entry.asSymbol()));
                                }
                            }
                            if (!remappedExceptionDescs.isEmpty()) {
                               methodBuilder.with(java.lang.classfile.attribute.ExceptionsAttribute.ofSymbols(remappedExceptionDescs));
                            }
                        } else {
                            methodBuilder.with((java.lang.classfile.MethodElement) attr);
                        }
                    }
                    mm.code().ifPresent(codeModel -> {
                        methodBuilder.withCode(codeBodyBuilder -> MajestikClassLoader.transformCode(codeBodyBuilder, codeModel, REMAP_FUNCTION));
                    });
                });
            }
            */
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
            /*
            else if (ce instanceof NestHostAttribute nha) {
                 ClassDesc remappedHostDesc = REMAP_FUNCTION.apply(nha.nestHost().asSymbol());
                 classBuilder.with(java.lang.classfile.attribute.NestHostAttribute.of(cp.classEntry(remappedHostDesc)));
            } else if (ce instanceof NestMembersAttribute nma) {
                List<ClassDesc> remappedMemberDescs = new ArrayList<>();
                List<java.lang.classfile.constantpool.ClassEntry> memberEntries = nma.nestMembers();
                if (memberEntries != null) {
                    for (java.lang.classfile.constantpool.ClassEntry entry : memberEntries) {
                        remappedMemberDescs.add(REMAP_FUNCTION.apply(entry.asSymbol()));
                    }
                }
                if(!remappedMemberDescs.isEmpty()) {
                   classBuilder.with(java.lang.classfile.attribute.NestMembersAttribute.ofSymbols(remappedMemberDescs));
                }
            } else if (ce instanceof PermittedSubclassesAttribute psa) {
                List<ClassDesc> remappedSubclassDescs = new ArrayList<>();
                List<java.lang.classfile.constantpool.ClassEntry> subclassEntries = psa.permittedSubclasses();
                if (subclassEntries != null) {
                    for (java.lang.classfile.constantpool.ClassEntry entry : subclassEntries) {
                        remappedSubclassDescs.add(REMAP_FUNCTION.apply(entry.asSymbol()));
                    }
                }
                if(!remappedSubclassDescs.isEmpty()) {
                    java.lang.classfile.attribute.PermittedSubclassesAttribute newAttr = java.lang.classfile.attribute.PermittedSubclassesAttribute.ofSymbols(remappedSubclassDescs);
                    classBuilder.with(newAttr);
                }
            }
            // General Class-Level Attribute Fallback
            else if (ce instanceof Attribute<?> attr &&
                     !(ce instanceof ClassFileVersion || ce instanceof Superclass || ce instanceof Interfaces ||
                       ce instanceof FieldModel || ce instanceof MethodModel || ce instanceof InnerClassesAttribute ||
                       ce instanceof NestHostAttribute || ce instanceof NestMembersAttribute || ce instanceof PermittedSubclassesAttribute)) {
                classBuilder.with((ClassFileElement) attr);
            }
            */
        }
    });
  }

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
}

package com.keronic;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException; // If needed for lambdas throwing IOException
import java.lang.classfile.*;
import java.lang.classfile.attribute.*;
import java.lang.classfile.constantpool.*;
import java.lang.classfile.instruction.*;
import java.lang.classfile.ClassFileElement;
import java.lang.classfile.FieldElement;
import java.lang.classfile.MethodElement;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.nio.file.Files;
import java.util.Optional;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        java.io.InputStream is = getResourceAsStream(classFileName);
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
            // Elements like ThisClass, ClassFileVersion are handled by the builder's initialization
            if (ce instanceof Superclass sc) {
                sc.superclassEntry() // Returns Optional<ClassEntry>
                    .map(ClassEntry::asSymbol)
                    .map(REMAP_FUNCTION)
                    .ifPresent(classBuilder::withSuperclass);
            } else if (ce instanceof Interfaces ifs) {
                List<ClassDesc> remappedInterfaceDescs = ifs.interfaces().stream()
                        .map((java.lang.classfile.constantpool.ClassEntry entry) -> entry.asSymbol())
                        .map(REMAP_FUNCTION)
                        .collect(Collectors.toList());
                if (!remappedInterfaceDescs.isEmpty()) {
                    classBuilder.withInterfaceSymbols(remappedInterfaceDescs);
                }
            } else if (ce instanceof FieldModel fm) {
                ClassDesc originalFieldTypeDesc = ClassDesc.ofDescriptor(fm.fieldType().stringValue());
                ClassDesc remappedFieldTypeDesc = remapClassDescRecursively(originalFieldTypeDesc, REMAP_FUNCTION);
                classBuilder.withField(fm.fieldName().stringValue(), remappedFieldTypeDesc, fieldBuilder -> {
                    fieldBuilder.withFlags(fm.flags().flagsMask());
                    for (Attribute<?> attr : fm.attributes()) {
                        fieldBuilder.with((FieldElement)attr);
                    }
                });
            } else if (ce instanceof MethodModel mm) {
                MethodTypeDesc originalMethodTypeDesc = mm.methodTypeSymbol();
                MethodTypeDesc remappedMethodTypeDesc = MethodTypeDesc.of(
                        remapClassDescRecursively(originalMethodTypeDesc.returnType(), REMAP_FUNCTION),
                        originalMethodTypeDesc.parameterList().stream()
                                .map(pt -> remapClassDescRecursively(pt, REMAP_FUNCTION))
                                .toArray(ClassDesc[]::new)
                );
                classBuilder.withMethod(mm.methodName().stringValue(), remappedMethodTypeDesc, mm.flags().flagsMask(), methodBuilder -> {
                    // Iterate mm.attributes() for specific attribute handling
                    for (Attribute<?> attr : mm.attributes()) {
                        if (attr instanceof ExceptionsAttribute ea) {
                            List<ClassDesc> remappedExceptionDescs = ea.exceptions().stream()
                                .map(ClassEntry::asSymbol)
                                .map(REMAP_FUNCTION)
                                .collect(Collectors.toList());
                            if (!remappedExceptionDescs.isEmpty()) {
                               methodBuilder.with(ExceptionsAttribute.ofSymbols(remappedExceptionDescs));
                            }
                        } else if (!(attr instanceof CodeAttribute)) { // CodeAttribute handled by withCode
                            methodBuilder.with((MethodElement)attr);
                        }
                    }
                    mm.code().ifPresent(codeModel -> { // This handles CodeAttribute
                        methodBuilder.withCode(codeBodyBuilder -> MajestikClassLoader.transformCode(codeBodyBuilder, codeModel, REMAP_FUNCTION));
                    });
                });
            } else if (ce instanceof InnerClassesAttribute ica) {
                List<java.lang.classfile.attribute.InnerClassesAttribute.InnerClassInfo> remappedInnerClasses = new ArrayList<>();
                for (java.lang.classfile.attribute.InnerClassesAttribute.InnerClassInfo entry : ica.classes()) {
                    ClassDesc innerClsSym = entry.innerClass().asSymbol();
                    ClassDesc remappedInnerClsSym = REMAP_FUNCTION.apply(innerClsSym);

                    Optional<ClassEntry> remappedOuterEntryOpt = entry.outerClass()
                        .map(ClassEntry::asSymbol)
                        .map(REMAP_FUNCTION)
                        .map(cp::classEntry);

                    remappedInnerClasses.add(java.lang.classfile.attribute.InnerClassesAttribute.InnerClassInfo.of(
                        cp.classEntry(remappedInnerClsSym),
                        remappedOuterEntryOpt,
                        entry.innerName().map(name -> cp.utf8Entry(name.stringValue())),
                        entry.accessFlags()
                    ));
                }
                if (!remappedInnerClasses.isEmpty()) {
                    java.lang.classfile.attribute.InnerClassesAttribute newInnerClassesAttribute =
                        java.lang.classfile.attribute.InnerClassesAttribute.of(remappedInnerClasses);
                    classBuilder.with(newInnerClassesAttribute);
                }
            } else if (ce instanceof NestHostAttribute nha) {
                 ClassDesc remappedHostDesc = REMAP_FUNCTION.apply(nha.nestHost().asSymbol());
                 classBuilder.with(NestHostAttribute.of(cp.classEntry(remappedHostDesc)));
            } else if (ce instanceof NestMembersAttribute nma) {
                 List<ClassDesc> remappedMemberDescs = nma.nestMembers().stream()
                    .map(ClassEntry::asSymbol)
                    .map(REMAP_FUNCTION)
                    .collect(Collectors.toList());
                 if(!remappedMemberDescs.isEmpty()) {
                    classBuilder.with(NestMembersAttribute.ofSymbols(remappedMemberDescs));
                 }
            } else if (ce instanceof PermittedSubclassesAttribute psa) {
                List<ClassDesc> remappedSubclassDescs = psa.permittedSubclasses().stream()
                    .map(ClassEntry::asSymbol)
                    .map(REMAP_FUNCTION)
                    .collect(Collectors.toList());
                if(!remappedSubclasses.isEmpty()) {
                    classBuilder.with(PermittedSubclassesAttribute.ofSymbols(remappedSubclasses)); // .with()
                }
            }
            // Pass through other direct class attributes and unhandled elements
            // Explicitly skip ThisClass and ClassFileVersion as they are handled by .build() and .withVersion()
            else if (!(ce instanceof ClassFileVersion)) { // ThisClass is not an element type, it's part of ClassModel
                 if (ce instanceof Attribute<?> attr) {
                     classBuilder.with(attr);
                 }
                 // Potentially other ClassElements if any, though most are attributes or models.
                 // else { classBuilder.with(ce); } // Use with caution for non-attribute, non-model elements
            }
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
            boolean isInterface = instr.isInterface(); // From original instruction

            switch (opcode) {
                case INVOKEVIRTUAL -> codeBuilder.invokevirtual(remappedOwner, methodName, remappedType);
                case INVOKESPECIAL -> codeBuilder.invokespecial(remappedOwner, methodName, remappedType, isInterface);
                case INVOKESTATIC -> codeBuilder.invokestatic(remappedOwner, methodName, remappedType, isInterface);
                case INVOKEINTERFACE -> codeBuilder.invokeinterface(remappedOwner, methodName, remappedType);
                case INVOKEDYNAMIC -> codeBuilder.with(instr); // Pass through
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
            ClassDesc originalType = instr.type().asSymbol(); // type() returns ClassEntry
            ClassDesc remappedType = remapFunction.apply(originalType);
            if (opcode == Opcode.INSTANCEOF) {
                codeBuilder.instanceofOp(remappedType);
            } else if (opcode == Opcode.CHECKCAST) {
                codeBuilder.checkcast(remappedType);
            } else {
                 codeBuilder.with(instr);
            }
        } else if (element instanceof NewObjectInstruction instr) {
            ClassDesc originalType = instr.className().asSymbol();
            ClassDesc remappedType = remapFunction.apply(originalType);
            codeBuilder.newOp(remappedType);
        } else if (element instanceof NewReferenceArrayInstruction instr) {
            ClassDesc componentType = instr.componentType().asSymbol();
            ClassDesc remappedComponentType = remapFunction.apply(componentType);
            codeBuilder.anewarray(remappedComponentType); // Corrected
        } else if (element instanceof NewMultiArrayInstruction instr) {
             ClassDesc originalArrayType = instr.arrayType().asSymbol();
             ClassDesc remappedArrayType = remapClassDescRecursively(originalArrayType, remapFunction);
             codeBuilder.multianewarray(remappedArrayType, instr.dimensions()); // Corrected
        } else if (element instanceof ConstantInstruction instr) {
            java.lang.constant.ConstantDesc constDesc = instr.constantValue(); // Corrected: constantValue()
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
                codeBuilder.ldc(constDesc); // Pass other ConstantDescs as is
            }
        }
        else {
            codeBuilder.with(element); // Pass through other instructions/pseudo-instructions
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

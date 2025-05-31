package com.keronic;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException; // If needed for lambdas throwing IOException
import java.lang.classfile.*;
import java.lang.classfile.attribute.*;
import java.lang.classfile.constantpool.*;
import java.lang.classfile.instruction.*;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.MethodTypeDesc;
import java.nio.file.Files;
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

    // ClassDesc remappedThisClass = REMAP_FUNCTION.apply(classModel.thisClass().asSymbol()); // ThisClass handled in ClassTransform

    ClassTransform classTransform = (classBuilder, classElement) -> {
        // Handle ThisClass explicitly if remapping it for the builder
        if (classElement instanceof ThisClass tc) {
            classBuilder.thisClass(REMAP_FUNCTION.apply(tc.thisClass().asSymbol()));
        } else if (classElement instanceof Superclass sc) {
            sc.superclassSymbol().ifPresent(superClsDesc ->
                classBuilder.withSuperclass(REMAP_FUNCTION.apply(superClsDesc))
            );
        } else if (classElement instanceof Interfaces ifs) {
            List<ClassDesc> remappedInterfaceDescs = ifs.interfaces().stream()
                    .map(cpEntry -> ((ClassEntry)cpEntry).asSymbol())
                    .map(REMAP_FUNCTION)
                    .collect(Collectors.toList());
            if (!remappedInterfaceDescs.isEmpty()) {
                classBuilder.withInterfaceSymbols(remappedInterfaceDescs);
            }
        } else if (classElement instanceof FieldModel fm) {
            ClassDesc originalFieldTypeDesc = ClassDesc.ofDescriptor(fm.fieldType().stringValue());
            ClassDesc remappedFieldTypeDesc = remapClassDescRecursively(originalFieldTypeDesc, REMAP_FUNCTION);
            classBuilder.withField(fm.fieldName().stringValue(), remappedFieldTypeDesc, fieldBuilder -> {
                fieldBuilder.withFlags(fm.flags().flagsMask());
                for (Attribute<?> attr : fm.attributes()) {
                    fieldBuilder.withAttribute(attr); // Pass attributes (incl. Signature - known simplification)
                }
            });
        } else if (classElement instanceof MethodModel mm) {
            MethodTypeDesc originalMethodTypeDesc = mm.methodTypeSymbol();
            MethodTypeDesc remappedMethodTypeDesc = MethodTypeDesc.of(
                    remapClassDescRecursively(originalMethodTypeDesc.returnType(), REMAP_FUNCTION),
                    originalMethodTypeDesc.parameterList().stream()
                            .map(pt -> remapClassDescRecursively(pt, REMAP_FUNCTION))
                            .toArray(ClassDesc[]::new)
            );
            classBuilder.withMethod(mm.methodName().stringValue(), remappedMethodTypeDesc, mm.flags().flagsMask(), methodBuilder -> {
                for (Attribute<?> attr : mm.attributes()) {
                    if (attr instanceof CodeAttribute) {
                        // Handled by withCode below
                    } else if (attr instanceof ExceptionsAttribute ea) {
                        List<ClassDesc> remappedExceptionDescs = ea.exceptionSymbols().stream() // Use exceptionSymbols()
                            .map(REMAP_FUNCTION)
                            .collect(Collectors.toList());
                        if (!remappedExceptionDescs.isEmpty()) {
                           methodBuilder.withAttribute(ExceptionsAttribute.ofSymbols(remappedExceptionDescs));
                        }
                    } else {
                        methodBuilder.withAttribute(attr); // Pass other attributes (incl. Signature)
                    }
                }
                mm.code().ifPresent(codeModel -> {
                    methodBuilder.withCode(codeBodyBuilder -> MajestikClassLoader.transformCode(codeBodyBuilder, codeModel, REMAP_FUNCTION));
                });
            });
        } else if (classElement instanceof InnerClassesAttribute ica) {
            List<InnerClassesAttribute.Entry> remappedInnerClasses = new ArrayList<>();
            ConstantPoolBuilder cp = classBuilder.constantPool();
            for (InnerClassesAttribute.Entry entry : ica.entries()) { // entries() and Entry type
                ClassDesc innerDesc = REMAP_FUNCTION.apply(entry.innerClass().asSymbol());
                // ClassDesc outerDesc = entry.outerClass().map(ClassEntry::asSymbol).map(REMAP_FUNCTION).orElse(null); // Handled below
                remappedInnerClasses.add(InnerClassesAttribute.Entry.of(
                    cp.classEntry(innerDesc),
                    entry.outerClass().map(oc -> cp.classEntry(REMAP_FUNCTION.apply(oc.asSymbol()))), // Remap outer if present
                    entry.innerName().map(in -> cp.utf8Entry(in.stringValue())), // innerName is Utf8Entry
                    entry.flags()
                ));
            }
            if (!remappedInnerClasses.isEmpty()) {
                classBuilder.withAttribute(InnerClassesAttribute.ofEntries(remappedInnerClasses)); // ofEntries
            }
        } else if (classElement instanceof NestHostAttribute nha) {
             ClassDesc remappedHostDesc = REMAP_FUNCTION.apply(nha.nestHost().asSymbol());
             classBuilder.withAttribute(NestHostAttribute.of(classBuilder.constantPool().classEntry(remappedHostDesc))); // of(ClassEntry)
        } else if (classElement instanceof NestMembersAttribute nma) {
             List<ClassDesc> remappedMembers = nma.memberSymbols().stream() // memberSymbols()
                .map(REMAP_FUNCTION)
                .collect(Collectors.toList());
             if(!remappedMembers.isEmpty()) {
                classBuilder.withAttribute(NestMembersAttribute.ofSymbols(remappedMembers));
             }
        } else if (classElement instanceof PermittedSubclassesAttribute psa) {
            List<ClassDesc> remappedSubclasses = psa.permittedSubclassSymbols().stream() // permittedSubclassSymbols()
                .map(REMAP_FUNCTION)
                .collect(Collectors.toList());
            if(!remappedSubclasses.isEmpty()) {
                classBuilder.withAttribute(PermittedSubclassesAttribute.ofSymbols(remappedSubclasses));
            }
        } else if (classElement instanceof ClassFileVersion cfv) {
             // ClassBuilder handles version by default, or use classBuilder.withVersion()
        } else if (classElement instanceof ModuleAttribute || classElement instanceof ModulePackagesAttribute || classElement instanceof ModuleMainClassAttribute) {
            // Pass module attributes as is for now, complexity of remapping package names inside is high.
            classBuilder.with(classElement);
        }
        // Ensure other attributes are passed through.
        // Elements that are not models or specifically handled attributes.
        else if (classElement instanceof Attribute<?> attr) {
             classBuilder.withAttribute(attr);
        }
        // Other class elements (if any new ones appear or are not attributes/models)
        // else { classBuilder.with(classElement); } // Use with caution
    };

    return ClassFile.of(ClassFile.ConstantPoolSharingOption.NEW_POOL) // Or SHARED_POOL and rely on CPBuilder to make new entries
            .transform(classModel, classTransform);
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
                            .toArray(ClassDesc[]::new)
            );
            String methodName = instr.name().stringValue();

            switch (opcode) {
                case INVOKEVIRTUAL -> codeBuilder.invokevirtual(remappedOwner, methodName, remappedType);
                case INVOKESPECIAL -> codeBuilder.invokespecial(remappedOwner, methodName, remappedType, instr.isInterface());
                case INVOKESTATIC -> codeBuilder.invokestatic(remappedOwner, methodName, remappedType, instr.isInterface());
                case INVOKEINTERFACE -> codeBuilder.invokeinterface(remappedOwner, methodName, remappedType);
                case INVOKEDYNAMIC -> {
                    // INVOKEDYNAMIC is complex. Pass through for now.
                    codeBuilder.with(instr);
                }
                default -> codeBuilder.with(instr); // Should not happen for InvokeInstruction
            }
        } else if (element instanceof FieldInstruction instr) {
            Opcode opcode = instr.opcode();
            ClassDesc owner = instr.owner().asSymbol();
            ClassDesc remappedOwner = remapFunction.apply(owner);
            ClassDesc fieldType = instr.typeSymbol(); // typeSymbol() gives field's type
            ClassDesc remappedFieldType = remapClassDescRecursively(fieldType, remapFunction);
            String fieldName = instr.name().stringValue();

            switch (opcode) {
                case GETSTATIC -> codeBuilder.getstatic(remappedOwner, fieldName, remappedFieldType);
                case PUTSTATIC -> codeBuilder.putstatic(remappedOwner, fieldName, remappedFieldType);
                case GETFIELD -> codeBuilder.getfield(remappedOwner, fieldName, remappedFieldType);
                case PUTFIELD -> codeBuilder.putfield(remappedOwner, fieldName, remappedFieldType);
                default -> codeBuilder.with(instr); // Should not happen
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
                 codeBuilder.with(instr); // Should not happen for TypeCheckInstruction
            }
        } else if (element instanceof NewObjectInstruction instr) {
            ClassDesc originalType = instr.className().asSymbol(); // className() is correct
            ClassDesc remappedType = remapFunction.apply(originalType);
            codeBuilder.newObject(remappedType); // Use newObject()
        } else if (element instanceof NewReferenceArrayInstruction instr) { // anewarray
            ClassDesc componentType = instr.componentType().asSymbol(); // componentType() is correct
            ClassDesc remappedComponentType = remapFunction.apply(componentType);
            codeBuilder.newReferenceArray(remappedComponentType); // Use newReferenceArray()
        } else if (element instanceof NewMultiArrayInstruction instr) { // multianewarray
             ClassDesc originalArrayType = instr.arrayType().asSymbol();
             ClassDesc remappedArrayType = remapClassDescRecursively(originalArrayType, remapFunction);
             codeBuilder.newMultiArrayInstruction(remappedArrayType, instr.dimensions());
        } else if (element instanceof ConstantInstruction instr) { // Handles LDC, LDC_W, LDC2_W
            ConstantDesc constDesc = instr.constantDesc(); // Use constantDesc()
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

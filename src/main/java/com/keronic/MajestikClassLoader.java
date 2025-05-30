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

    ClassDesc remappedThisClass = REMAP_FUNCTION.apply(classModel.thisClass().asSymbol());

    return ClassFile.of(ClassFile.ConstantPoolSharingOption.NEW_POOL)
        .transform(classModel, remappedThisClass, (classBuilder, classElement) -> {
          // Default behavior: pass through elements not explicitly handled.
          // Specific handlers below will take precedence.
          if (classElement instanceof Superclass sc) {
            if (sc.superclass().isPresent()) {
              classBuilder.withSuperclass(REMAP_FUNCTION.apply(sc.superclass().get().asSymbol()));
            }
          } else if (classElement instanceof Interfaces ifs) {
            List<ClassDesc> remappedInterfaceDescs = ifs.interfaces().stream()
                .map(cpEntry -> ((ClassEntry) cpEntry).asSymbol())
                .map(REMAP_FUNCTION)
                .collect(Collectors.toList());
            if (!remappedInterfaceDescs.isEmpty()) {
              classBuilder.withInterfaceSymbols(remappedInterfaceDescs);
            }
          } else if (classElement instanceof FieldModel fm) {
            ClassDesc originalFieldTypeDesc = ClassDesc.ofDescriptor(fm.fieldType().stringValue());
            ClassDesc remappedFieldTypeDesc = remapClassDescRecursively(originalFieldTypeDesc, REMAP_FUNCTION);
            classBuilder.withField(fm.fieldName().stringValue(), remappedFieldTypeDesc, fb -> {
              fb.withFlags(fm.flags().flagsMask());
              for (Attribute<?> attr : fm.attributes()) {
                // SignatureAttribute is a known simplification: pass as is.
                fb.withAttribute(attr);
              }
            });
          } else if (classElement instanceof MethodModel mm) {
            MethodTypeDesc originalMethodTypeDesc = mm.methodTypeSymbol();
            MethodTypeDesc remappedMethodTypeDesc = MethodTypeDesc.of(
                remapClassDescRecursively(originalMethodTypeDesc.returnType(), REMAP_FUNCTION),
                originalMethodTypeDesc.parameterList().stream()
                    .map(pt -> remapClassDescRecursively(pt, REMAP_FUNCTION))
                    .toArray(ClassDesc[]::new));

            classBuilder.withMethod(mm.methodName().stringValue(), remappedMethodTypeDesc, mm.flags().flagsMask(), mb -> {
              for (Attribute<?> attr : mm.attributes()) {
                if (attr instanceof CodeAttribute) {
                  // CodeAttribute is handled by transforming its model via withCode
                } else if (attr instanceof ExceptionsAttribute ea) {
                  List<ClassDesc> remappedExceptionDescs = ea.exceptions().stream()
                      .map(cpEntry -> ((ClassEntry) cpEntry).asSymbol())
                      .map(REMAP_FUNCTION)
                      .collect(Collectors.toList());
                  if (!remappedExceptionDescs.isEmpty()) {
                    mb.withAttribute(ExceptionsAttribute.ofSymbols(remappedExceptionDescs));
                  }
                } else {
                  // Other attributes (e.g., SignatureAttribute - passed as is)
                  mb.withAttribute(attr);
                }
              }
              mm.code().ifPresent(codeModel -> {
                mb.withCode(codeBuilder -> MajestikClassLoader.transformCode(codeBuilder, codeModel, REMAP_FUNCTION));
              });
            });
          } else if (classElement instanceof InnerClassesAttribute ica) {
            List<InnerClassesAttribute.InnerClassInfo> remappedInnerClasses = new ArrayList<>();
            for (InnerClassesAttribute.InnerClassInfo ici : ica.classes()) {
              ClassDesc innerDesc = REMAP_FUNCTION.apply(ici.innerClass().asSymbol());
              ClassDesc outerDesc = ici.outerClass().map(ClassEntry::asSymbol).map(REMAP_FUNCTION).orElse(null);
              ConstantPoolBuilder cpBuilder = classBuilder.constantPool(); // Use ClassBuilder's CP
              remappedInnerClasses.add(InnerClassesAttribute.InnerClassInfo.of(
                  cpBuilder.classEntry(innerDesc),
                  outerDesc == null ? null : cpBuilder.classEntry(outerDesc),
                  ici.innerName().map(Utf8Entry::stringValue).map(cpBuilder::utf8Entry).orElse(null),
                  ici.flags()));
            }
            if (!remappedInnerClasses.isEmpty()) {
              classBuilder.withAttribute(InnerClassesAttribute.of(remappedInnerClasses));
            }
          } else if (classElement instanceof NestHostAttribute nha) {
            classBuilder.withAttribute(NestHostAttribute.ofSymbol(REMAP_FUNCTION.apply(nha.nestHost().asSymbol())));
          } else if (classElement instanceof NestMembersAttribute nma) {
            List<ClassDesc> remappedMembers = nma.nestMembers().stream()
                .map(ClassEntry::asSymbol)
                .map(REMAP_FUNCTION)
                .collect(Collectors.toList());
            if (!remappedMembers.isEmpty()) {
              classBuilder.withAttribute(NestMembersAttribute.ofSymbols(remappedMembers));
            }
          } else if (classElement instanceof PermittedSubclassesAttribute psa) {
            List<ClassDesc> remappedSubclasses = psa.permittedSubclasses().stream()
                .map(ClassEntry::asSymbol)
                .map(REMAP_FUNCTION)
                .collect(Collectors.toList());
            if (!remappedSubclasses.isEmpty()) {
              classBuilder.withAttribute(PermittedSubclassesAttribute.ofSymbols(remappedSubclasses));
            }
          }
          // Attributes like SourceFile, SourceDebugExtension, etc. are typically passed through.
          // SignatureAttribute on the class itself.
          else if (classElement instanceof SignatureAttribute sa) {
            // Passing signature as-is (known simplification)
            classBuilder.withAttribute(sa);
          } else if (classElement instanceof ClassFileVersion cfv) {
            // This is usually automatically handled by the ClassBuilder when starting.
            // Explicitly passing it might be redundant or restricted.
            // classBuilder.with(classElement); // Or ignore, as builder sets version.
          } else if (classElement instanceof Attribute) {
            // Catch-all for other direct class attributes
            classBuilder.withAttribute((Attribute<?>) classElement);
          }
          // Non-attribute elements that are not explicitly handled should be rare at class level
          // else { classBuilder.with(classElement); } // Use with caution
        });
  }

  private static void transformCode(CodeBuilder codeBuilder, CodeModel codeModel, Function<ClassDesc, ClassDesc> remapFunction) {
    for (CodeElement element : codeModel) {
      if (element instanceof InvokeInstruction instr) {
        ClassDesc owner = instr.owner().asSymbol();
        ClassDesc remappedOwner = remapFunction.apply(owner);
        MethodTypeDesc type = instr.typeSymbol();
        MethodTypeDesc remappedType = MethodTypeDesc.of(
            remapClassDescRecursively(type.returnType(), remapFunction),
            type.parameterList().stream()
                .map(p -> remapClassDescRecursively(p, remapFunction))
                .toArray(ClassDesc[]::new));
        codeBuilder.invokeInstruction(instr.opcode(), remappedOwner, instr.name().stringValue(), remappedType, instr.isInterface());
      } else if (element instanceof FieldInstruction instr) {
        ClassDesc owner = instr.owner().asSymbol();
        ClassDesc remappedOwner = remapFunction.apply(owner);
        ClassDesc type = instr.typeSymbol();
        ClassDesc remappedType = remapClassDescRecursively(type, remapFunction);
        codeBuilder.fieldInstruction(instr.opcode(), remappedOwner, instr.name().stringValue(), remappedType);
      } else if (element instanceof TypeCheckInstruction instr) {
        ClassDesc type = instr.typeSymbol();
        ClassDesc remappedType = remapFunction.apply(type); // Direct type, not recursive, e.g. AASTORE
        if (instr.opcode() == Opcode.INSTANCEOF || instr.opcode() == Opcode.CHECKCAST) {
          codeBuilder.typeCheckInstruction(instr.opcode(), remappedType);
        } else { // AASTORE - component type is not directly part of instruction, but type of array ref on stack
          codeBuilder.with(element); // Pass AASTORE as is
        }
      } else if (element instanceof NewObjectInstruction instr) {
        ClassDesc type = instr.className().asSymbol();
        ClassDesc remappedType = remapFunction.apply(type);
        codeBuilder.newObjectInstruction(remappedType);
      } else if (element instanceof NewReferenceArrayInstruction instr) { // anewarray
        ClassDesc componentType = instr.componentType().asSymbol();
        ClassDesc remappedComponentType = remapFunction.apply(componentType); // Remap only the component type
        codeBuilder.newReferenceArrayInstruction(remappedComponentType);
      } else if (element instanceof NewMultiArrayInstruction instr) { // multianewarray
        ClassDesc arrayType = instr.arrayType().asSymbol(); // This is the full array type
        ClassDesc remappedArrayType = remapClassDescRecursively(arrayType, remapFunction);
        codeBuilder.newMultiArrayInstruction(remappedArrayType.elementType(), instr.dimensions()); // Must be element type
      } else if (element instanceof ConstantInstruction instr) {
        Object constVal = instr.constantValue();
        if (constVal instanceof ClassDesc cdConst) {
          codeBuilder.constantInstruction(remapClassDescRecursively(cdConst, remapFunction));
        } else if (constVal instanceof MethodTypeDesc mtdConst) {
          MethodTypeDesc remappedMtd = MethodTypeDesc.of(
              remapClassDescRecursively(mtdConst.returnType(), remapFunction),
              mtdConst.parameterList().stream()
                  .map(p -> remapClassDescRecursively(p, remapFunction))
                  .toArray(ClassDesc[]::new));
          codeBuilder.constantInstruction(remappedMtd);
        } else {
          codeBuilder.with(element);
        }
      } else if (element instanceof LoadInstruction load && Opcode.LDC == load.opcode()) {
        PoolEntry cpEntry = load.constant();
        if (cpEntry instanceof ClassEntry ce) {
          ClassDesc originalDesc = ce.asSymbol();
          ClassDesc remappedDesc = remapFunction.apply(originalDesc);
          // Ensure using the codeBuilder's constant pool for the new entry
          codeBuilder.ldc(codeBuilder.constantPool().classEntry(remappedDesc));
        } else if (cpEntry instanceof MethodTypeEntry mte) {
          MethodTypeDesc originalDesc = mte.asSymbol();
          MethodTypeDesc remappedMtd = MethodTypeDesc.of(
              remapClassDescRecursively(originalDesc.returnType(), remapFunction),
              originalDesc.parameterList().stream()
                  .map(p -> remapClassDescRecursively(p, remapFunction))
                  .toArray(ClassDesc[]::new));
          codeBuilder.ldc(codeBuilder.constantPool().methodTypeEntry(remappedMtd));
        }
        // Add other LDC-able types if necessary (MethodHandleEntry, DynamicConstantEntry)
        else {
          codeBuilder.with(element); // Pass other LDC types
        }
      } else {
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

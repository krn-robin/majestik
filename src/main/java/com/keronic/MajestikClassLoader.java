package com.keronic;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;
import java.lang.constant.ClassDesc;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.ClassElement;
import java.lang.classfile.ClassBuilder;
import java.lang.classfile.attribute.InnerClassesAttribute;
import java.lang.classfile.constantpool.ConstantPoolBuilder;
import java.lang.classfile.constantpool.ClassEntry;
import java.lang.classfile.constantpool.Utf8Entry;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.lang.classfile.ClassFileVersion; // Added as it's used in the uncommented code
import java.lang.classfile.CodeBuilder;
import java.lang.classfile.CodeElement;
import java.lang.classfile.CodeModel;
import java.lang.classfile.MethodModel;
import java.lang.classfile.Opcode;
import java.lang.classfile.attribute.CodeAttribute;
import java.lang.classfile.instruction.ConstantInstruction;
import java.lang.classfile.instruction.FieldInstruction;
import java.lang.classfile.instruction.InvokeInstruction;
import java.lang.classfile.instruction.NewMultiArrayInstruction;
import java.lang.classfile.instruction.NewObjectInstruction;
import java.lang.classfile.instruction.NewReferenceArrayInstruction;
import java.lang.classfile.instruction.TypeCheckInstruction;
import java.lang.constant.ConstantDesc;
import java.lang.constant.MethodTypeDesc;


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
            } else if (ce instanceof MethodModel currentMethod) {
                classBuilder.withMethod(currentMethod.methodName(), currentMethod.methodType(), currentMethod.flags().flagsMask(), mb -> {
                    for (var attr : currentMethod.attributes()) {
                        if (attr instanceof CodeAttribute codeAttr) {
                            // Ensure codeName is passed correctly if needed, or use default.
                            // The CodeBuilder from withCodeAttribute will be used by transformCode.
                            mb.withCodeAttribute(codeAttr.codeName(), caBuilder -> {
                                transformCode(caBuilder, codeAttr.code(), REMAP_FUNCTION);
                            });
                        } else {
                            mb.withAttribute(attr);
                        }
                    }
                });
            } else {
                classBuilder.with(ce); // Add other elements to the builder
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
            boolean isInterface = instr.isInterface();

            switch (opcode) {
                case INVOKEVIRTUAL -> codeBuilder.invokevirtual(remappedOwner, methodName, remappedType);
                case INVOKESPECIAL -> codeBuilder.invokespecial(remappedOwner, methodName, remappedType, isInterface);
                case INVOKESTATIC -> codeBuilder.invokestatic(remappedOwner, methodName, remappedType, isInterface);
                case INVOKEINTERFACE -> codeBuilder.invokeinterface(remappedOwner, methodName, remappedType);
                case INVOKEDYNAMIC -> codeBuilder.with(instr); // Keep INVOKEDYNAMIC as is for now
                default -> codeBuilder.with(instr); // Pass through other instructions
            }
        } else if (element instanceof FieldInstruction instr) {
            Opcode opcode = instr.opcode();
            ClassDesc owner = instr.owner().asSymbol();
            ClassDesc remappedOwner = remapFunction.apply(owner);
            ClassDesc fieldType = instr.typeSymbol(); // This is ClassDesc for fields
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
            ClassDesc originalType = instr.type().asSymbol(); // This is ClassDesc
            ClassDesc remappedType = remapFunction.apply(originalType); // Remap directly if it's a simple class
            // If originalType could be an array, remapClassDescRecursively might be needed,
            // but type() for TypeCheckInstruction (INSTANCEOF, CHECKCAST) is usually a ClassDesc of a class or interface.
            // Let's assume remapFunction handles array types if necessary, or this is fine.
            // The existing remapClassDescRecursively handles arrays, so we should use it.
            remappedType = remapClassDescRecursively(originalType, remapFunction);


            if (opcode == Opcode.INSTANCEOF) {
                codeBuilder.instanceofInstruction(remappedType);
            } else if (opcode == Opcode.CHECKCAST) {
                codeBuilder.checkcast(remappedType);
            } else {
                 codeBuilder.with(instr); // Should not happen for TypeCheckInstruction
            }
        } else if (element instanceof NewObjectInstruction instr) {
            ClassDesc originalType = instr.className().asSymbol(); // className() returns a ClassEntry
            ClassDesc remappedType = remapFunction.apply(originalType); // Apply base remap
            // As NewObjectInstruction is for non-array types, direct remapFunction is okay.
            codeBuilder.newObject(remappedType);
        } else if (element instanceof NewReferenceArrayInstruction instr) { // anewarray
            ClassDesc componentType = instr.componentType().asSymbol(); // componentType() returns ClassEntry
            // componentType for anewarray is the element type, which can be a class, interface, or array type itself.
            // So, recursive remapping is essential.
            ClassDesc remappedComponentType = remapClassDescRecursively(componentType, remapFunction);
            codeBuilder.anewarray(remappedComponentType);
        } else if (element instanceof NewMultiArrayInstruction instr) { // multianewarray
             ClassDesc originalArrayType = instr.arrayType().asSymbol(); // arrayType returns ClassEntry
             // The arrayType for multianewarray is the full array type (e.g., [[Ljava/lang/String;)
             // Recursive remapping is essential here.
             ClassDesc remappedArrayType = remapClassDescRecursively(originalArrayType, remapFunction);
             codeBuilder.multianewarray(remappedArrayType, instr.dimensions());
        } else if (element instanceof ConstantInstruction instr) { // ldc
            ConstantDesc constDesc = instr.constantValue();
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
            // Add handling for DynamicConstantDescs if they involve ClassDesc or MethodTypeDesc
            // For now, other constant types are passed as is.
            else {
                codeBuilder.ldc(constDesc);
            }
        }
        else {
            // Pass through other CodeElements like Labels, LineNumbers, etc.
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
      if (componentType == remappedComponentType) { // Referential equality check is fine here
        return cd;
      }
      // Rebuild the array structure with the new component type
      // int dimensions = 0; // Not needed, ClassDesc.arrayType(int) or ClassDesc.arrayType() handles this
      // ClassDesc temp = cd;
      // while (temp.isArray()) {
      //   dimensions++;
      //   temp = temp.componentType();
      // }
      // ClassDesc newArrayDesc = remappedComponentType;
      // for (int i = 0; i < dimensions; i++) {
      //   newArrayDesc = newArrayDesc.arrayType();
      // }
      // A simpler way if we know the original array depth or reconstruct:
      // If cd is X[][], and remappedComponentType is Y, we need Y[][]
      // ClassDesc.ofDescriptor(cd.descriptorString().replace(componentType.descriptorString(), remappedComponentType.descriptorString())) might be too simplistic.
      // The current approach of rebuilding based on depth is more robust.
      // Let's refine:
      String descriptor = cd.descriptorString();
      int arrayDimensions = 0;
      while(descriptor.charAt(arrayDimensions) == '[') {
          arrayDimensions++;
      }
      ClassDesc newArrayDesc = remappedComponentType;
      for (int i = 0; i < arrayDimensions; i++) {
          newArrayDesc = newArrayDesc.arrayType();
      }
      return newArrayDesc;
    }
    // Apply the remap function for non-array, non-primitive types
    return remapFunction.apply(cd);
  }
}

package com.keronic;

import module java.base;

import java.lang.classfile.Annotation;

/** */
public class MajestikClassLoader extends ClassLoader {

  private final Function<ClassDesc, ClassDesc> REMAP_FUNCTION =
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
        ClassLoadingResult result = this.loadClassData(name);
        // Since loadClassData is expected to return byte[], and now it won't,
        // this defineClass call will fail if cd is null or not a byte array.
        // For the purpose of this FQN test, we might need to make loadClassData return null
        // and handle that in findClass, or let it throw an IOException that findClass catches.
        // The subtask implies loadClassData should still declare `throws IOException`.
        if (result == null
            || result.getClassBytes()
                == null) { // Or check if it's the dummy byte array if we made one
          throw new ClassNotFoundException(
              name + " (Majestik: loadClassData returned null or empty bytes)");
        }
        return super.defineClass(
            result.getRemappedClassName(),
            result.getClassBytes(),
            0,
            result.getClassBytes().length);
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

  private static class ClassLoadingResult {
    final byte[] classBytes;
    final String remappedClassName;

    ClassLoadingResult(byte[] classBytes, String remappedClassName) {
      this.classBytes = classBytes;
      this.remappedClassName = remappedClassName;
    }

    byte[] getClassBytes() {
      return classBytes;
    }

    String getRemappedClassName() {
      return remappedClassName;
    }
  }

  private ClassLoadingResult loadClassData(String className) throws IOException {
    var classFileName = className.replace('.', File.separatorChar) + ".class";
    byte[] classBytes;
    try {
      Path path = Path.of(classFileName);
      if (!Files.exists(path)) {
        InputStream is = getResourceAsStream(classFileName);
        if (is == null) {
          throw new IOException(
              "Class file not found: " + classFileName + " via direct path or resource stream.");
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

    byte[] transformedBytes =
        ClassFile.of()
            .build(
                remappedThisClassDesc,
                classBuilder -> {
                  // Apply original model's version and flags (excluding ThisClass name)
                  if (classModel.majorVersion() > 0) {
                    classBuilder.withVersion(classModel.majorVersion(), classModel.minorVersion());
                  }
                  classBuilder.withFlags(classModel.flags().flagsMask());

                  for (ClassElement ce : classModel) { // classModel is iterable
                    if (ce instanceof ClassFileVersion) {
                      // Version is set via classBuilder.withVersion(...) earlier
                      continue;
                    } else if (ce instanceof Superclass sc) {
                      if (sc.superclassEntry() != null) {
                        ClassDesc remappedSuperDesc = mapTypeDesc(sc.superclassEntry().asSymbol());
                        if (remappedSuperDesc != null
                            && !remappedSuperDesc.equals(sc.superclassEntry().asSymbol())) {
                          classBuilder.withSuperclass(remappedSuperDesc);
                        } else {
                          classBuilder.with(sc);
                        }
                      } else {
                        classBuilder.with(sc);
                      }
                    } else if (ce instanceof Interfaces ins) {
                      List<ClassEntry> remappedInterfaces =
                          ins.interfaces().stream()
                              .map(
                                  ifaceEntry ->
                                      classBuilder
                                          .constantPool()
                                          .classEntry(mapTypeDesc(ifaceEntry.asSymbol())))
                              .toList();
                      boolean interfacesChanged =
                          !listEquals(
                              ins.interfaces(),
                              remappedInterfaces,
                              (orig, rem) -> orig.asSymbol().equals(rem.asSymbol()));
                      if (interfacesChanged) {
                        classBuilder.withInterfaces(remappedInterfaces);
                      } else {
                        classBuilder.with(ins);
                      }
                    } else if (ce instanceof SignatureAttribute sigAttr) {
                      Utf8Entry originalSignatureEntry = sigAttr.signature();
                      Utf8Entry remappedSignatureEntry =
                          mapSignatureString(originalSignatureEntry, classBuilder.constantPool());
                      if (originalSignatureEntry != remappedSignatureEntry) {
                        classBuilder.with(SignatureAttribute.of(remappedSignatureEntry));
                      } else {
                        classBuilder.with(sigAttr);
                      }
                    } else if (ce instanceof InnerClassesAttribute ica) {
                      List<InnerClassInfo> remappedInnerClasses =
                          ica.classes().stream()
                              .map(
                                  ici -> {
                                    ClassDesc originalInner = ici.innerClass().asSymbol();
                                    Optional<ClassDesc> originalOuter =
                                        ici.outerClass().map(ClassEntry::asSymbol);
                                    Optional<Utf8Entry> originalInnerName = ici.innerName();
                                    int flags = ici.flagsMask();

                                    ClassDesc remappedInner = mapTypeDesc(originalInner);
                                    Optional<ClassDesc> remappedOuter =
                                        originalOuter.map(this::mapTypeDesc);

                                    if (originalInner != remappedInner
                                        || !originalOuter.equals(remappedOuter)) {
                                      Optional<ClassEntry> remappedOuterEntry =
                                          remappedOuter.map(
                                              cd -> classBuilder.constantPool().classEntry(cd));
                                      return InnerClassInfo.of(
                                          classBuilder.constantPool().classEntry(remappedInner),
                                          remappedOuterEntry,
                                          originalInnerName,
                                          flags);
                                    }
                                    return ici;
                                  })
                              .toList();

                      boolean innerClassesChanged =
                          !listEquals(ica.classes(), remappedInnerClasses, (o, r) -> o == r);
                      if (innerClassesChanged) {
                        classBuilder.with(InnerClassesAttribute.of(remappedInnerClasses));
                      } else {
                        classBuilder.with(ica);
                      }
                    } else if (ce instanceof EnclosingMethodAttribute ema) {
                      ClassDesc remappedEnclosingClass =
                          mapTypeDesc(ema.enclosingClass().asSymbol());
                      Optional<NameAndTypeEntry> originalMethodNat = ema.enclosingMethod();
                      Optional<NameAndTypeEntry> remappedMethodNat =
                          originalMethodNat.map(
                              nat -> {
                                MethodTypeDesc originalTypeDesc =
                                    MethodTypeDesc.ofDescriptor(nat.type().stringValue());
                                MethodTypeDesc remappedDesc = mapMethodDesc(originalTypeDesc);
                                // If type changed, create new NameAndTypeEntry
                                return originalTypeDesc == remappedDesc
                                    ? nat
                                    : classBuilder
                                        .constantPool()
                                        .nameAndTypeEntry(nat.name().stringValue(), remappedDesc);
                              });

                      if (!remappedEnclosingClass.equals(ema.enclosingClass().asSymbol())
                          || !originalMethodNat.equals(remappedMethodNat)) {
                        classBuilder.with(
                            EnclosingMethodAttribute.of(
                                classBuilder.constantPool().classEntry(remappedEnclosingClass),
                                remappedMethodNat));
                      } else {
                        classBuilder.with(ema);
                      }
                    } else if (ce instanceof RuntimeVisibleAnnotationsAttribute rva) {
                      List<Annotation> remappedAnnotations =
                          mapAnnotations(rva.annotations(), classBuilder.constantPool());
                      if (!listEquals(rva.annotations(), remappedAnnotations, (o, r) -> o == r)) {
                        classBuilder.with(
                            RuntimeVisibleAnnotationsAttribute.of(remappedAnnotations));
                      } else {
                        classBuilder.with(rva);
                      }
                    } else if (ce instanceof RuntimeInvisibleAnnotationsAttribute ria) {
                      List<Annotation> remappedAnnotations =
                          mapAnnotations(ria.annotations(), classBuilder.constantPool());
                      if (!listEquals(ria.annotations(), remappedAnnotations, (o, r) -> o == r)) {
                        classBuilder.with(
                            RuntimeInvisibleAnnotationsAttribute.of(remappedAnnotations));
                      } else {
                        classBuilder.with(ria);
                      }
                    } else if (ce instanceof NestHostAttribute nha) {
                      ClassDesc remappedHost = mapTypeDesc(nha.nestHost().asSymbol());
                      if (!remappedHost.equals(nha.nestHost().asSymbol())) {
                        classBuilder.with(
                            NestHostAttribute.of(
                                classBuilder.constantPool().classEntry(remappedHost)));
                      } else {
                        classBuilder.with(nha);
                      }
                    } else if (ce instanceof NestMembersAttribute nma) {
                      List<ClassEntry> remappedMembers =
                          nma.nestMembers().stream()
                              .map(
                                  memberEntry ->
                                      classBuilder
                                          .constantPool()
                                          .classEntry(mapTypeDesc(memberEntry.asSymbol())))
                              .toList();
                      if (!listEquals(
                          nma.nestMembers(),
                          remappedMembers,
                          (o, r) -> o.asSymbol().equals(r.asSymbol()))) {
                        classBuilder.with(NestMembersAttribute.of(remappedMembers));
                      } else {
                        classBuilder.with(nma);
                      }
                    } else if (ce instanceof PermittedSubclassesAttribute psa) {
                      List<ClassEntry> remappedSubclasses =
                          psa.permittedSubclasses().stream()
                              .map(
                                  subclassEntry ->
                                      classBuilder
                                          .constantPool()
                                          .classEntry(mapTypeDesc(subclassEntry.asSymbol())))
                              .toList();
                      if (!listEquals(
                          psa.permittedSubclasses(),
                          remappedSubclasses,
                          (o, r) -> o.asSymbol().equals(r.asSymbol()))) {
                        classBuilder.with(PermittedSubclassesAttribute.of(remappedSubclasses));
                      } else {
                        classBuilder.with(psa);
                      }
                    } else if (ce instanceof MethodModel mm) {
                      classBuilder.withMethod(
                          mm.methodName().stringValue(),
                          mapMethodDesc(mm.methodTypeSymbol()),
                          mm.flags().flagsMask(), // Restore flags as a direct parameter
                          mb ->
                              mb.transform(mm, createMethodRemappingTransform(mb.constantPool())));
                    } else if (ce instanceof FieldModel fm) {
                      classBuilder.withField(
                          fm.fieldName().stringValue(),
                          mapTypeDesc(fm.fieldTypeSymbol()), // Field type is remapped here
                          // No flags argument here
                          fb -> {
                            fb.withFlags(fm.flags().flagsMask()); // Flags set here
                            fb.transform(fm, createFieldRemappingTransform(fb.constantPool()));
                          });
                    } else {
                      classBuilder.with(ce);
                    }
                  }
                });

    String remappedNameStr;
    if (remappedThisClassDesc.isArray()) {
      // For arrays, descriptorString is already close to binary name (e.g., "[Ljava/lang/String;")
      // Class.getName() for arrays returns the descriptor string (e.g. "[Ljava.lang.String;").
      // Ensure internal slashes are dots for defineClass if that's the expectation for array
      // component FQNs.
      // However, descriptorString usually uses slashes for internal names.
      // ClassLoader.defineClass expects dot-separated names for the class itself.
      // For arrays, "[[Lcom.foo.Bar;" is the binary name.
      // descriptorString() for ClassDesc like "com/foo/Bar[]" is "[Lcom/foo/Bar;"
      // So, this should be correct.
      remappedNameStr = remappedThisClassDesc.descriptorString().replace('/', '.');
    } else if (remappedThisClassDesc.isPrimitive()) {
      // For primitives, displayName is the name (e.g., "int", "float")
      remappedNameStr = remappedThisClassDesc.displayName();
    } else { // Class or Interface
      String packageName = remappedThisClassDesc.packageName();
      String simpleClassName = remappedThisClassDesc.displayName(); // Unqualified name
      if (packageName.isEmpty()) {
        remappedNameStr = simpleClassName;
      } else {
        remappedNameStr = packageName + "." + simpleClassName;
      }
    }
    return new ClassLoadingResult(transformedBytes, remappedNameStr);
  }

  // New instance method
  private ClassDesc mapTypeDesc(ClassDesc desc) {
    if (desc == null) return null;
    if (desc.isPrimitive()) {
      return desc;
    }
    if (desc.isArray()) {
      ClassDesc ultimateElementType = desc;
      int rank = 0;
      while (ultimateElementType.isArray()) {
        ultimateElementType = ultimateElementType.componentType();
        rank++;
      }
      ClassDesc remappedUltimateElementType = mapTypeDesc(ultimateElementType);

      if (ultimateElementType == remappedUltimateElementType) {
        return desc;
      } else {
        return remappedUltimateElementType.arrayType(rank);
      }
    }
    return this.REMAP_FUNCTION.apply(desc);
  }

  // New instance method
  private MethodTypeDesc mapMethodDesc(MethodTypeDesc desc) {
    if (desc == null) return null;
    ClassDesc remappedReturnType = mapTypeDesc(desc.returnType());
    List<ClassDesc> remappedParameterTypes =
        desc.parameterList().stream().map(this::mapTypeDesc).toList();
    boolean changed =
        !remappedReturnType.equals(desc.returnType())
            || !remappedParameterTypes.equals(desc.parameterList());

    if (changed) {
      return MethodTypeDesc.of(
          remappedReturnType, remappedParameterTypes.toArray(new ClassDesc[0]));
    } else {
      return desc;
    }
  }

  private CodeTransform createCodeRemappingTransform() { // Renamed, made instance, no params
    return (cob, coe) -> { // cob is CodeBuilder, coe is CodeElement
      switch (coe) {
        case InvokeInstruction instr:
          cob.invoke(
              instr.opcode(),
              this.mapTypeDesc(instr.owner().asSymbol()),
              instr.name().stringValue(),
              this.mapMethodDesc(instr.typeSymbol()),
              instr.isInterface());
          break;
        case FieldInstruction fai:
          cob.fieldAccess(
              fai.opcode(),
              this.mapTypeDesc(fai.owner().asSymbol()),
              fai.name().stringValue(),
              this.mapTypeDesc(fai.typeSymbol()));
          break;
        case TypeCheckInstruction instr:
          Opcode typeCheckOpcode = instr.opcode();
          ClassDesc remappedTypeForTypeCheck = this.mapTypeDesc(instr.type().asSymbol());
          if (typeCheckOpcode == Opcode.INSTANCEOF) {
            cob.instanceOf(remappedTypeForTypeCheck);
          } else if (typeCheckOpcode == Opcode.CHECKCAST) {
            cob.checkcast(remappedTypeForTypeCheck);
          } else {
            cob.with(instr);
          }
          break;
        case NewObjectInstruction instr:
          cob.new_(this.mapTypeDesc(instr.className().asSymbol()));
          break;
        case NewReferenceArrayInstruction instr:
          cob.anewarray(this.mapTypeDesc(instr.componentType().asSymbol()));
          break;
        case NewMultiArrayInstruction instr:
          cob.multianewarray(this.mapTypeDesc(instr.arrayType().asSymbol()), instr.dimensions());
          break;
        case ConstantInstruction.LoadConstantInstruction ldcInstr:
          ConstantDesc originalConstant = ldcInstr.constantValue();
          ConstantDesc remappedConstant = mapConstantDesc(originalConstant, cob.constantPool());
          if (remappedConstant != originalConstant) {
            cob.ldc(remappedConstant);
          } else {
            cob.with(ldcInstr);
          }
          break;
        case InvokeDynamicInstruction idi:
          {
            DirectMethodHandleDesc originalBootstrapMethod = idi.bootstrapMethod();
            List<ConstantDesc> originalBootstrapArgs = idi.bootstrapArgs(); // Corrected
            String methodName = idi.name().stringValue(); // Corrected
            MethodTypeDesc originalMethodType = idi.typeSymbol();

            DirectMethodHandleDesc remappedBootstrapMethod =
                mapDirectMethodHandle(originalBootstrapMethod, cob.constantPool());
            List<ConstantDesc> remappedBootstrapArgs =
                originalBootstrapArgs.stream()
                    .map(arg -> mapConstantDesc(arg, cob.constantPool()))
                    .toList();
            MethodTypeDesc remappedMethodType = mapMethodDesc(originalMethodType);

            boolean bootstrapMethodChanged = (originalBootstrapMethod != remappedBootstrapMethod);
            boolean methodTypeChanged = (originalMethodType != remappedMethodType);
            boolean bootstrapArgsChanged =
                !listEquals(originalBootstrapArgs, remappedBootstrapArgs, (o, r) -> o == r);

            if (bootstrapMethodChanged || methodTypeChanged || bootstrapArgsChanged) {
              cob.invokedynamic(
                  DynamicCallSiteDesc.of(
                      remappedBootstrapMethod,
                      methodName,
                      remappedMethodType,
                      remappedBootstrapArgs.toArray(new ConstantDesc[0])));
            } else {
              cob.with(idi);
            }
            break;
          }
        default:
          cob.with(coe);
          break;
      }
    };
  }

  private FieldTransform createFieldRemappingTransform(ConstantPoolBuilder fieldCp) {
    return (fieldBuilder, fieldElement) -> {
      switch (fieldElement) {
        case SignatureAttribute sigAttr:
          {
            Utf8Entry originalSignatureEntry = sigAttr.signature();
            Utf8Entry remappedSignatureEntry = mapSignatureString(originalSignatureEntry, fieldCp);
            if (originalSignatureEntry != remappedSignatureEntry) {
              fieldBuilder.with(SignatureAttribute.of(remappedSignatureEntry));
            } else {
              fieldBuilder.with(sigAttr);
            }
            break;
          }
        case RuntimeVisibleAnnotationsAttribute rva:
          {
            List<Annotation> remappedAnnotations = mapAnnotations(rva.annotations(), fieldCp);
            if (remappedAnnotations != rva.annotations()) {
              fieldBuilder.with(RuntimeVisibleAnnotationsAttribute.of(remappedAnnotations));
            } else {
              fieldBuilder.with(rva);
            }
            break;
          }
        case RuntimeInvisibleAnnotationsAttribute ria:
          {
            List<Annotation> remappedAnnotations = mapAnnotations(ria.annotations(), fieldCp);
            if (remappedAnnotations != ria.annotations()) {
              fieldBuilder.with(RuntimeInvisibleAnnotationsAttribute.of(remappedAnnotations));
            } else {
              fieldBuilder.with(ria);
            }
            break;
          }
        // ConstantValueAttribute will fall to default
        default:
          fieldBuilder.with(fieldElement);
          break;
      }
    };
  }

  private MethodTransform createMethodRemappingTransform(ConstantPoolBuilder methodCp) {
    return (methodBuilder, methodElement) -> {
      switch (methodElement) {
        case CodeAttribute codeAttr:
          methodBuilder.transformCode(codeAttr, createCodeRemappingTransform());
          break;
        case SignatureAttribute sigAttr:
          {
            Utf8Entry originalSignatureEntry = sigAttr.signature();
            Utf8Entry remappedSignatureEntry = mapSignatureString(originalSignatureEntry, methodCp);
            if (originalSignatureEntry != remappedSignatureEntry) {
              methodBuilder.with(SignatureAttribute.of(remappedSignatureEntry));
            } else {
              methodBuilder.with(sigAttr);
            }
            break;
          }
        case ExceptionsAttribute ea:
          {
            List<ClassEntry> remappedExceptions =
                ea.exceptions().stream()
                    .map(e -> methodCp.classEntry(mapTypeDesc(e.asSymbol())))
                    .toList();
            boolean changed =
                ea.exceptions().size() != remappedExceptions.size()
                    || !listEquals(
                        ea.exceptions(),
                        remappedExceptions,
                        (o, r) -> o.asSymbol().equals(r.asSymbol()));
            if (changed) {
              methodBuilder.with(ExceptionsAttribute.of(remappedExceptions));
            } else {
              methodBuilder.with(ea);
            }
            break;
          }
        case RuntimeVisibleAnnotationsAttribute rva:
          {
            List<Annotation> remappedAnnotations = mapAnnotations(rva.annotations(), methodCp);
            if (remappedAnnotations != rva.annotations()) {
              methodBuilder.with(RuntimeVisibleAnnotationsAttribute.of(remappedAnnotations));
            } else {
              methodBuilder.with(rva);
            }
            break;
          }
        case RuntimeInvisibleAnnotationsAttribute ria:
          {
            List<Annotation> remappedAnnotations = mapAnnotations(ria.annotations(), methodCp);
            if (remappedAnnotations != ria.annotations()) {
              methodBuilder.with(RuntimeInvisibleAnnotationsAttribute.of(remappedAnnotations));
            } else {
              methodBuilder.with(ria);
            }
            break;
          }
        default:
          methodBuilder.with(methodElement);
          break;
      }
    };
  }

  // remapClassDescRecursively is now mapTypeDesc (instance method)
  // The old static version is removed by this diff.

  private ConstantDesc mapConstantDesc(ConstantDesc constantDesc, ConstantPoolBuilder cp) {
    if (constantDesc == null) {
      return null;
    }

    return switch (constantDesc) {
      case ClassDesc cd -> mapTypeDesc(cd); // mapTypeDesc returns ClassDesc
      case MethodTypeDesc mtd -> mapMethodDesc(mtd); // mapMethodDesc returns MethodTypeDesc
      case String originalString -> {
        String remappedString = originalString;
        // Revised String remapping logic for general strings:
        String tempRemappedString =
            originalString.replace("com.gesmallworld.magik", "com.keronic.majestik");
        if (!originalString.equals(tempRemappedString)) {
          remappedString = tempRemappedString;
        } else {
          // Try slash form only if dot form didn't match
          tempRemappedString =
              originalString.replace("com/gesmallworld/magik", "com/keronic/majestik");
          if (!originalString.equals(tempRemappedString)) {
            remappedString = tempRemappedString;
          }
        }

        if (!originalString.equals(remappedString)) {
          yield remappedString; // Yield the remapped String object directly
        } else {
          yield originalString; // Yield the original String object
        }
      }
      case DirectMethodHandleDesc dmhd -> mapDirectMethodHandle(dmhd, cp);
      case DynamicConstantDesc<?> dcd -> mapDynamicConstantDesc(dcd, cp);
      default -> constantDesc;
    };
  }

  private DirectMethodHandleDesc mapDirectMethodHandle(
      DirectMethodHandleDesc dmhd, ConstantPoolBuilder cp) {
    if (dmhd == null) {
      return null;
    }

    ClassDesc remappedOwner = mapTypeDesc(dmhd.owner());
    String methodName = dmhd.methodName();

    boolean ownerChanged = (remappedOwner != dmhd.owner());

    switch (dmhd.kind()) {
      case GETTER, SETTER, STATIC_GETTER, STATIC_SETTER:
        ClassDesc fieldType = ClassDesc.ofDescriptor(dmhd.lookupDescriptor());
        ClassDesc remappedFieldType = mapTypeDesc(fieldType);
        if (ownerChanged || fieldType != remappedFieldType) {
          return MethodHandleDesc.ofField(
              dmhd.kind(), remappedOwner, methodName, remappedFieldType);
        }
        break;
      default:
        MethodTypeDesc methodType = MethodTypeDesc.ofDescriptor(dmhd.lookupDescriptor());
        MethodTypeDesc remappedMethodType = mapMethodDesc(methodType);
        if (ownerChanged || methodType != remappedMethodType) {
          return MethodHandleDesc.ofMethod(
              dmhd.kind(), remappedOwner, methodName, remappedMethodType);
        }
        break;
    }
    return dmhd;
  }

  private DynamicConstantDesc<?> mapDynamicConstantDesc(
      DynamicConstantDesc<?> dcd, ConstantPoolBuilder cp) {
    if (dcd == null) {
      return null;
    }

    DirectMethodHandleDesc originalBootstrapMethod = dcd.bootstrapMethod();
    DirectMethodHandleDesc remappedBootstrapMethod =
        mapDirectMethodHandle(originalBootstrapMethod, cp);

    String constantName = dcd.constantName();

    ClassDesc originalConstantType = dcd.constantType();
    ClassDesc remappedConstantType = mapTypeDesc(originalConstantType);

    List<ConstantDesc> originalBootstrapArgs = dcd.bootstrapArgsList();
    List<ConstantDesc> remappedBootstrapArgs =
        originalBootstrapArgs.stream().map(arg -> mapConstantDesc(arg, cp)).toList();

    boolean bootstrapMethodChanged = (originalBootstrapMethod != remappedBootstrapMethod);
    boolean constantTypeChanged = (originalConstantType != remappedConstantType);
    boolean bootstrapArgsChanged =
        !listEquals(originalBootstrapArgs, remappedBootstrapArgs, (o, r) -> o == r);

    if (bootstrapMethodChanged || constantTypeChanged || bootstrapArgsChanged) {
      return DynamicConstantDesc.ofNamed(
          remappedBootstrapMethod,
          constantName,
          remappedConstantType,
          remappedBootstrapArgs.toArray(new ConstantDesc[0]));
    } else {
      return dcd;
    }
  }

  private <T> boolean listEquals(List<T> list1, List<T> list2, BiPredicate<T, T> equality) {
    if (list1 == list2) return true;
    if (list1.size() != list2.size()) return false;
    for (int i = 0; i < list1.size(); i++) {
      if (!equality.test(list1.get(i), list2.get(i))) {
        return false;
      }
    }
    return true;
  }

  // Helper method for mapAnnotations, assuming it's similar to how other list transformations are
  // handled
  private List<Annotation> mapAnnotations(
      List<Annotation> originalAnnotations, ConstantPoolBuilder cp) {
    List<Annotation> remappedAnnotations =
        originalAnnotations.stream().map(ann -> mapAnnotation(ann, cp)).toList();
    // Determine if any annotation actually changed.
    boolean changed = false;
    for (int i = 0; i < originalAnnotations.size(); i++) {
      if (originalAnnotations.get(i)
          != remappedAnnotations.get(
              i)) { // Relies on mapAnnotation returning original if no change
        changed = true;
        break;
      }
    }
    return changed ? remappedAnnotations : originalAnnotations;
  }

  private AnnotationValue mapAnnotationValue(AnnotationValue val, ConstantPoolBuilder cp) {
    if (val == null) return null;
    return switch (val) {
      case AnnotationValue.OfAnnotation oa ->
          AnnotationValue.ofAnnotation(mapAnnotation(oa.annotation(), cp));
      case AnnotationValue.OfArray arr -> {
        List<AnnotationValue> remappedValues =
            arr.values().stream().map(v -> mapAnnotationValue(v, cp)).toList();
        // Check if any value actually changed to avoid unnecessary new array creation
        boolean changed = false;
        if (remappedValues.size() != arr.values().size()) { // Should not happen with toList()
          changed = true;
        } else {
          for (int i = 0; i < remappedValues.size(); i++) {
            if (remappedValues.get(i)
                != arr.values()
                    .get(
                        i)) { // Reference check, assumes mapAnnotationValue returns original if no
                              // change
              changed = true;
              break;
            }
          }
        }
        yield changed ? AnnotationValue.ofArray(remappedValues) : arr;
      }
      case AnnotationValue.OfClass oc -> {
        ClassDesc originalClassDesc = oc.classSymbol();
        ClassDesc remappedClassDesc = mapTypeDesc(originalClassDesc);
        yield originalClassDesc == remappedClassDesc
            ? oc
            : AnnotationValue.ofClass(remappedClassDesc);
      }
      case AnnotationValue.OfEnum oe -> {
        ClassDesc originalEnumClassDesc = oe.classSymbol();
        ClassDesc remappedEnumClassDesc = mapTypeDesc(originalEnumClassDesc);
        // Name typically doesn't need remapping unless it's also a class name pattern
        yield originalEnumClassDesc == remappedEnumClassDesc
            ? oe
            : AnnotationValue.ofEnum(remappedEnumClassDesc, oe.constantName().stringValue());
      }
      // Default for OfConstant (String, int, long, float, double, short, byte, char, boolean)
      // These typically don't contain class names that need remapping in this context.
      default -> val;
    };
  }

  private Annotation mapAnnotation(Annotation ann, ConstantPoolBuilder cp) {
    if (ann == null) return null;

    ClassDesc originalAnnotationType = ann.classSymbol();
    ClassDesc remappedAnnotationType = mapTypeDesc(originalAnnotationType);

    List<AnnotationElement> originalElements = ann.elements();
    List<AnnotationElement> remappedElements =
        originalElements.stream()
            .map(
                el -> {
                  AnnotationValue originalValue = el.value();
                  AnnotationValue remappedValue = mapAnnotationValue(originalValue, cp);
                  // If value changed, create new element, else return original
                  return originalValue == remappedValue
                      ? el
                      : AnnotationElement.of(el.name(), remappedValue);
                })
            .toList();

    boolean typeChanged = originalAnnotationType != remappedAnnotationType;
    boolean elementsChanged = false;
    if (originalElements.size() != remappedElements.size()) { // Should not happen
      elementsChanged = true;
    } else {
      for (int i = 0; i < originalElements.size(); i++) {
        if (originalElements.get(i) != remappedElements.get(i)) { // Reference check
          elementsChanged = true;
          break;
        }
      }
    }

    if (typeChanged || elementsChanged) {
      return Annotation.of(remappedAnnotationType, remappedElements);
    } else {
      return ann; // Return original if nothing changed
    }
  }

  private Utf8Entry mapSignatureString(Utf8Entry signatureUtf8Entry, ConstantPoolBuilder cp) {
    if (signatureUtf8Entry == null) {
      return null;
    }
    String originalSignature = signatureUtf8Entry.stringValue();
    String remappedSignature = originalSignature;

    // Order matters: specific (with L) before general
    remappedSignature =
        remappedSignature.replace("Lcom/gesmallworld/magik/", "Lcom/keronic/majestik/");
    remappedSignature =
        remappedSignature.replace("com/gesmallworld/magik/", "com/keronic/majestik/");

    // Less common in signatures, but for safety:
    remappedSignature = remappedSignature.replace("com.gesmallworld.magik", "com.keronic.majestik");

    if (!originalSignature.equals(remappedSignature)) {
      return cp.utf8Entry(remappedSignature);
    } else {
      return signatureUtf8Entry;
    }
  }
}

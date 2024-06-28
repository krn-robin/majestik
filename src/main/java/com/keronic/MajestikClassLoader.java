/**
 *
 */
package com.keronic;

import java.io.File;
import java.io.IOException;
import java.lang.classfile.ClassFile;
import java.lang.classfile.ClassModel;
import java.lang.classfile.ClassTransform;
import java.lang.classfile.CodeTransform;
import java.lang.classfile.components.ClassRemapper;
import java.lang.classfile.instruction.InvokeDynamicInstruction;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.constant.MethodHandleDesc;
import java.lang.constant.MethodTypeDesc;
import java.nio.file.Path;
import java.util.function.Function;

/**
 *
 */
public class MajestikClassLoader extends ClassLoader {

	static final Function<ClassDesc, ClassDesc> REMAP_FUNCTION = cd -> ClassDesc
			.of(cd.packageName().replace("com.gesmallworld.magik", "com.keronic.majestik"), cd.displayName());

	public MajestikClassLoader() {
		// TODO Auto-generated constructor stub
		super("Majestik", getSystemClassLoader());
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (name.startsWith("magik") ||
				name.startsWith("majestik")) {
			try {
				var cd = this.loadClassData(name);
				return super.defineClass(name, cd, 0, cd.length);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return super.findClass(name);
	}

	ClassDesc map(ClassDesc cd) {
		return ClassRemapper.of(REMAP_FUNCTION).map(cd);
	}

	// Method copied from jdk.internal.classfile.imp.ClassRemapperImpl
	DirectMethodHandleDesc mapDirectMethodHandle(DirectMethodHandleDesc dmhd) {
		return switch (dmhd.kind()) {
			case GETTER, SETTER, STATIC_GETTER, STATIC_SETTER ->
				MethodHandleDesc.ofField(dmhd.kind(), map(dmhd.owner()),
						dmhd.methodName(),
						map(ClassDesc.ofDescriptor(dmhd.lookupDescriptor())));
			default ->
				MethodHandleDesc.ofMethod(dmhd.kind(), map(dmhd.owner()),
						dmhd.methodName(),
						mapMethodDesc(MethodTypeDesc.ofDescriptor(dmhd.lookupDescriptor())));
		};
	}

	// Method copied from jdk.internal.classfile.imp.ClassRemapperImpl
	MethodTypeDesc mapMethodDesc(MethodTypeDesc desc) {
		return MethodTypeDesc.of(map(desc.returnType()),
				desc.parameterList().stream().map(this::map).toArray(ClassDesc[]::new));
	}

	private byte[] loadClassData(String className) throws IOException {
		String fileName = className.replace('.', File.separatorChar) + ".class";

		ClassFile cf = ClassFile.of(ClassFile.ConstantPoolSharingOption.NEW_POOL);
		ClassModel cm = cf.parse(Path.of(fileName));

		var crm = ClassRemapper.of(REMAP_FUNCTION);

		CodeTransform bsmrm = (cob, coe) -> {
			switch (coe) {
				// JDK-8332505: ClassRemapper should take care of this, but it does not seem to
				// work for the bootstrapmethods
				// https://bugs.openjdk.org/browse/JDK-8332505
				case InvokeDynamicInstruction idi ->
					cob.invokedynamic(DynamicCallSiteDesc.of(
							mapDirectMethodHandle(idi.bootstrapMethod()), idi.name().stringValue(),
							mapMethodDesc(idi.typeSymbol()),
							idi.bootstrapArgs().stream().toArray(ConstantDesc[]::new)));
				default ->
					cob.with(coe);
			}
		};

		// TODO: ClassTransform.andThen() does not seem to work to chain the
		// transformations?
		byte[] newBytes = cf.transform(cf.parse(cf.transform(cm, crm)), ClassTransform.transformingMethodBodies(bsmrm));

		// FIXME: DEBUG
		//Files.write(new File("tmp/java_test/bla.class").toPath(), newBytes);

		return newBytes;
	}
}

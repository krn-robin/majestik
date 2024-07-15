package com.keronic.majestik.constant;

import java.lang.constant.ClassDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.MethodTypeDesc;

public final class ConstantDescs {

	public static final ClassDesc CD_double = java.lang.constant.ConstantDescs.CD_double;
	public static final ClassDesc CD_long = java.lang.constant.ConstantDescs.CD_long;
	public static final ClassDesc CD_CallSite = java.lang.constant.ConstantDescs.CD_CallSite;
	public static final ClassDesc CD_Double = java.lang.constant.ConstantDescs.CD_Double;
	public static final ClassDesc CD_Long = java.lang.constant.ConstantDescs.CD_Long;
	public static final ClassDesc CD_Object = java.lang.constant.ConstantDescs.CD_Object;
	public static final ClassDesc CD_String = java.lang.constant.ConstantDescs.CD_String;

	public static final ClassDesc CD_PrintStream = ClassDesc.of("java.io.PrintStream");
	public static final ClassDesc CD_System = ClassDesc.of("java.lang.System");
	public static final ClassDesc CD_ConstantBuilder = ClassDesc.of("com.keronic.majestik.language.invokers.ConstantBuilder");
	public static final ClassDesc CD_GlobalAccessor = ClassDesc.of("com.keronic.majestik.language.invokers.GlobalAccessor");
	public static final ClassDesc CD_ProcInvoker = ClassDesc.of("com.keronic.majestik.language.invokers.ProcInvoker");

	public static final DirectMethodHandleDesc BSM_STRING_BUILDER = java.lang.constant.ConstantDescs.ofCallsiteBootstrap(CD_ConstantBuilder,
			"stringBootstrap", CD_CallSite, CD_String);
	public static final DirectMethodHandleDesc BSM_GLOBAL_FETCHER = java.lang.constant.ConstantDescs.ofCallsiteBootstrap(CD_GlobalAccessor,
			"bootstrapFetcher2", CD_CallSite, CD_String, CD_String);
	public static final DirectMethodHandleDesc BSM_NATURAL_PROC = java.lang.constant.ConstantDescs.ofCallsiteBootstrap(CD_ProcInvoker,
			"naturalBootstrap", ConstantDescs.CD_CallSite);

	public static final MethodTypeDesc MTD_Object = MethodTypeDesc.of(ConstantDescs.CD_Object);
	public static final MethodTypeDesc MTD_ObjectObjectObject = MethodTypeDesc.of(ConstantDescs.CD_Object,
			ConstantDescs.CD_Object, ConstantDescs.CD_Object);
	public static final MethodTypeDesc MTD_Doubledouble = MethodTypeDesc.of(ConstantDescs.CD_Double,
			ConstantDescs.CD_double);
	public static final MethodTypeDesc MTD_Longlong = MethodTypeDesc.of(ConstantDescs.CD_Long,
			ConstantDescs.CD_long);
}

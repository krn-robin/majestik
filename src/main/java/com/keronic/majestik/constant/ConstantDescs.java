package com.keronic.majestik.constant;

import module java.base;

public final class ConstantDescs {
  public static final String INIT_NAME = java.lang.constant.ConstantDescs.INIT_NAME;
  public static final String CLASS_INIT_NAME = java.lang.constant.ConstantDescs.CLASS_INIT_NAME;

	public static final ClassDesc CD_double = java.lang.constant.ConstantDescs.CD_double;
  public static final ClassDesc CD_int = java.lang.constant.ConstantDescs.CD_int;
	public static final ClassDesc CD_long = java.lang.constant.ConstantDescs.CD_long;
  public static final ClassDesc CD_void = java.lang.constant.ConstantDescs.CD_void;

	public static final ClassDesc CD_CallSite = java.lang.constant.ConstantDescs.CD_CallSite;
  public static final ClassDesc CD_Class = java.lang.constant.ConstantDescs.CD_Class;
	public static final ClassDesc CD_Double = java.lang.constant.ConstantDescs.CD_Double;
	public static final ClassDesc CD_Long = java.lang.constant.ConstantDescs.CD_Long;
	public static final ClassDesc CD_Object = java.lang.constant.ConstantDescs.CD_Object;
	public static final ClassDesc CD_String = java.lang.constant.ConstantDescs.CD_String;

  public static final ClassDesc CD_ResultTuple =
      ClassDesc.of("com.keronic.majestik.language.ResultTuple");

  public static final ClassDesc CD_BinaryDispatcher =
      ClassDesc.of("com.keronic.majestik.language.invokers.BinaryDispatcher");
  public static final ClassDesc CD_ConstantBuilder =
      ClassDesc.of("com.keronic.majestik.language.invokers.ConstantBuilder");
  public static final ClassDesc CD_DynamicAccessor =
      ClassDesc.of("com.keronic.majestik.language.invokers.DynamicAccessor");
  public static final ClassDesc CD_GlobalAccessor =
      ClassDesc.of("com.keronic.majestik.language.invokers.GlobalAccessor");
  public static final ClassDesc CD_ProcInvoker =
      ClassDesc.of("com.keronic.majestik.language.invokers.ProcInvoker");
  public static final ClassDesc CD_TupleBuilder =
      ClassDesc.of("com.keronic.majestik.language.invokers.TupleBuilder");

  public static final DirectMethodHandleDesc DYNAMIC_STORER_BSM =
      java.lang.constant.ConstantDescs.ofCallsiteBootstrap(
          CD_DynamicAccessor, "bootstrapStorer", CD_CallSite, CD_String);

  public static final DirectMethodHandleDesc BSM_BINARY_DISPATCHER =
      java.lang.constant.ConstantDescs.ofCallsiteBootstrap(
          CD_BinaryDispatcher, "bootstrap", CD_CallSite);
  public static final DirectMethodHandleDesc BSM_GLOBAL_FETCHER =
      java.lang.constant.ConstantDescs.ofCallsiteBootstrap(
          CD_GlobalAccessor, "bootstrapFetcher2", CD_CallSite, CD_String, CD_String);
  public static final DirectMethodHandleDesc BSM_GLOBAL_STORER =
      java.lang.constant.ConstantDescs.ofCallsiteBootstrap(
          CD_GlobalAccessor, "bootstrapStorer2", CD_CallSite, CD_String, CD_String);
  public static final DirectMethodHandleDesc BSM_NATURAL_PROC =
      java.lang.constant.ConstantDescs.ofCallsiteBootstrap(
          CD_ProcInvoker, "naturalBootstrap", CD_CallSite);
  public static final DirectMethodHandleDesc BSM_PROC_EMPTY_ENV_LITERAL =
      java.lang.constant.ConstantDescs.ofCallsiteBootstrap(
          CD_ConstantBuilder,
          "procWithEmptyEnvBootstrap",
          CD_CallSite,
          CD_Class,
          CD_String,
          CD_String,
          CD_int,
          CD_int,
          CD_int,
          CD_int);
  public static final DirectMethodHandleDesc BSM_STRING_BUILDER =
      java.lang.constant.ConstantDescs.ofCallsiteBootstrap(
          CD_ConstantBuilder, "stringBootstrap", CD_CallSite, CD_String);
  public static final DirectMethodHandleDesc BSM_TUPLE_BUILDER =
      java.lang.constant.ConstantDescs.ofCallsiteBootstrap(
	      CD_TupleBuilder, "bootstrap", CD_CallSite);

  public static final MethodTypeDesc MTD_void = java.lang.constant.ConstantDescs.MTD_void;

  public static final MethodTypeDesc MTD_Doubledouble =
      MethodTypeDesc.of(ConstantDescs.CD_Double, ConstantDescs.CD_double);
  public static final MethodTypeDesc MTD_Longlong =
      MethodTypeDesc.of(ConstantDescs.CD_Long, ConstantDescs.CD_long);
  public static final MethodTypeDesc MTD_Object = MethodTypeDesc.of(ConstantDescs.CD_Object);
  public static final MethodTypeDesc MTD_ObjectObject =
      MethodTypeDesc.of(ConstantDescs.CD_Object, ConstantDescs.CD_Object);
  public static final MethodTypeDesc MTD_ObjectObjectObject =
      MethodTypeDesc.of(ConstantDescs.CD_Object, ConstantDescs.CD_Object, ConstantDescs.CD_Object);
  public static final MethodTypeDesc MTD_ResultTupleObject =
      MethodTypeDesc.of(ConstantDescs.CD_ResultTuple, ConstantDescs.CD_Object);
  public static final MethodTypeDesc MTD_voidObject =
      MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_Object);
  public static final MethodTypeDesc MTD_voidObjectObject =
    MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_Object, ConstantDescs.CD_Object);
  public static final MethodTypeDesc MTD_voidObjectObjectObject =
    MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_Object, ConstantDescs.CD_Object, ConstantDescs.CD_Object);
public static final MethodTypeDesc MTD_voidClassStringStringint =
  MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_Class,ConstantDescs.CD_String, ConstantDescs.CD_String, ConstantDescs.CD_int);
  public static final MethodTypeDesc MTD_voidString =
      MethodTypeDesc.of(ConstantDescs.CD_void, ConstantDescs.CD_String);
}

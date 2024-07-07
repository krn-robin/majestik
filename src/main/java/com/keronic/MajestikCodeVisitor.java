package com.keronic;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.ConstantDescs;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.constant.MethodTypeDesc;
import java.util.ArrayList;
import java.util.List;

import com.keronic.antlr4.MajestikBaseVisitor;
import com.keronic.antlr4.MajestikParser;

public class MajestikCodeVisitor extends MajestikBaseVisitor<Void> {

	static final ClassDesc CD_PrintStream = ClassDesc.of("java.io.PrintStream");
	static final ClassDesc CD_System = ClassDesc.of("java.lang.System");
	static final ClassDesc CD_ConstantBuilder = ClassDesc.of("com.keronic.majestik.language.invokers.ConstantBuilder");
	static final ClassDesc CD_GlobalAccessor = ClassDesc.of("com.keronic.majestik.language.invokers.GlobalAccessor");
	static final ClassDesc CD_ProcInvoker = ClassDesc.of("com.keronic.majestik.language.invokers.ProcInvoker");

	static final DirectMethodHandleDesc BSM_STRING_BUILDER = ConstantDescs.ofCallsiteBootstrap(CD_ConstantBuilder,
			"stringBootstrap", ConstantDescs.CD_CallSite, ConstantDescs.CD_String);
	static final DirectMethodHandleDesc BSM_GLOBAL_FETCHER = ConstantDescs.ofCallsiteBootstrap(CD_GlobalAccessor,
			"bootstrapFetcher2", ConstantDescs.CD_CallSite, ConstantDescs.CD_String, ConstantDescs.CD_String);
	static final DirectMethodHandleDesc BSM_NATURAL_PROC = ConstantDescs.ofCallsiteBootstrap(CD_ProcInvoker,
			"naturalBootstrap", ConstantDescs.CD_CallSite);

	static final MethodTypeDesc MTD_Object = MethodTypeDesc.of(ConstantDescs.CD_Object);
	static final MethodTypeDesc MTD_ObjectObjectObject = MethodTypeDesc.of(ConstantDescs.CD_Object,
			ConstantDescs.CD_Object, ConstantDescs.CD_Object);

	CodeBuilder cb;
	List<String> varList = new ArrayList<String>();

	/**
	 * Constructs a new MajestikCodeVisitor that utilizes a provided CodeBuilder.
	 * The CodeBuilder is essential for dynamically building and compiling code
	 * during the AST traversal.
	 * This design allows for flexible manipulation of code elements at runtime,
	 * which is critical for the functionalities implemented in the visit methods.
	 *
	 * @param cb The CodeBuilder instance that will be used for generating dynamic
	 *           code constructs.
	 */
	public MajestikCodeVisitor(CodeBuilder cb) {
		this.cb = cb;
	}

	@Override
	public Void visitString(MajestikParser.StringContext ctx) {
		String quotedString = ctx.getText();
		String pureString = normalizeString(quotedString);

		this.cb.invokedynamic(DynamicCallSiteDesc.of(BSM_STRING_BUILDER, "string", MTD_Object, pureString));
		return visitChildren(ctx);
	}

	private String normalizeString(String quotedString) {
	    assert quotedString.length() >= 2;
	    assert quotedString.charAt(0) == '\"' && quotedString.charAt(quotedString.length() - 1) == '\"' ||
	           quotedString.charAt(0) == '\'' && quotedString.charAt(quotedString.length() - 1) == '\'';
	    return quotedString.substring(1, quotedString.length() - 1);
	}

	@Override
	public Void visitBlock(MajestikParser.BlockContext ctx) {
		System.out.println("LOG: - Enter block");
		Void result = visitChildren(ctx);
		System.out.println("LOG: - leave block");
		return result;
	}

	@Override
	public Void visitInvoke(MajestikParser.InvokeContext ctx) {
		this.cb.invokedynamic(
				DynamicCallSiteDesc.of(BSM_GLOBAL_FETCHER, "fetch", MTD_Object, "sw", ctx.name.getText()));

		var result = visitChildren(ctx);

		// https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/invoke/CallSite.html

		System.out.println("LOG: - Compiling invoke...");
		this.cb.invokedynamic(DynamicCallSiteDesc.of(BSM_NATURAL_PROC, "()", MTD_ObjectObjectObject));

		return result;
	}

	@Override
	public Void visitAssign(MajestikParser.AssignContext ctx) {
		System.out.format("children %d\n", ctx.children.size());
		return visitChildren(ctx);
	}
}

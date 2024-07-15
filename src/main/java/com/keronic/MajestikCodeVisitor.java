package com.keronic;

import java.lang.classfile.CodeBuilder;
import java.lang.constant.ClassDesc;
import java.lang.constant.DirectMethodHandleDesc;
import java.lang.constant.DynamicCallSiteDesc;
import java.lang.constant.MethodTypeDesc;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.keronic.majestik.constant.ConstantDescs;
import com.keronic.antlr4.MajestikBaseVisitor;
import com.keronic.antlr4.MajestikParser;

public class MajestikCodeVisitor extends MajestikBaseVisitor<Void> {

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

		this.cb.invokedynamic(DynamicCallSiteDesc.of(ConstantDescs.BSM_STRING_BUILDER, "string", ConstantDescs.MTD_Object, pureString));
		return visitChildren(ctx);
	}

	private String normalizeString(String quotedString) {
		assert quotedString.length() >= 2;
		assert quotedString.charAt(0) == '\"' && quotedString.charAt(quotedString.length() - 1) == '\"' ||
				quotedString.charAt(0) == '\'' && quotedString.charAt(quotedString.length() - 1) == '\'';
		return quotedString.substring(1, quotedString.length() - 1);
	}

	@Override
	public Void visitNumber(MajestikParser.NumberContext ctx) {
		var numberString = ctx.getText();
		try {
			var number = NumberFormat.getInstance(Locale.ROOT).parse(numberString);
			if (number instanceof Long n) {
				this.cb.constantInstruction(n);
				this.cb.invokestatic(ConstantDescs.CD_Long, "valueOf", ConstantDescs.MTD_Longlong);
			} else if (number instanceof Double n) {
				this.cb.constantInstruction(n);
				this.cb.invokestatic(ConstantDescs.CD_Double, "valueOf", ConstantDescs.MTD_Doubledouble);
			}
		} catch (ParseException e) {
			throw new RuntimeException("Failed to parse number: " + numberString, e);
		}

		return visitChildren(ctx);
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
				DynamicCallSiteDesc.of(ConstantDescs.BSM_GLOBAL_FETCHER, "fetch", ConstantDescs.MTD_Object, "sw", ctx.name.getText()));

		var result = visitChildren(ctx);

		// https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/invoke/CallSite.html

		System.out.println("LOG: - Compiling invoke...");
		this.cb.invokedynamic(DynamicCallSiteDesc.of(ConstantDescs.BSM_NATURAL_PROC, "()", ConstantDescs.MTD_ObjectObjectObject));

		return result;
	}

	@Override
	public Void visitVar(MajestikParser.VarContext ctx) {
		if (ctx.parent instanceof MajestikParser.ArgumentContext) {
			var varname = ctx.getText();
			this.cb.aload(this.varList.indexOf(varname));
		}
		return visitChildren(ctx);
	}

	@Override
	public Void visitAssign(MajestikParser.AssignContext ctx) {
		var result = visitChildren(ctx);

		var varname = ctx.lhs().var().getText();

		if (!this.varList.contains(varname))
			this.varList.add(varname);

		this.cb.astore(this.varList.indexOf(varname));
		return result;
	}
}

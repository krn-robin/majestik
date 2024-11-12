package com.keronic;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;
import com.sonar.sslr.api.AstNode;
import java.lang.System.Logger.Level;
import nl.ramsolutions.sw.magik.MagikVisitor;
import nl.ramsolutions.sw.magik.api.MagikGrammar;

public class MajestikCodeVisitor extends MagikVisitor {
  private static final System.Logger LOGGER =
      System.getLogger(MethodHandles.lookup().lookupClass().getName());

  CodeBuilder cb;
  Map<String, Integer> varMap = new ConcurrentHashMap<>();

  /**
   * Constructs a new MajestikCodeVisitor that utilizes a provided CodeBuilder. The CodeBuilder is
   * essential for dynamically building and compiling code during the AST traversal. This design
   * allows for flexible manipulation of code elements at runtime, which is critical for the
   * functionalities implemented in the visit methods.
   *
   * @param cb The CodeBuilder instance that will be used for generating dynamic code constructs.
   */
  public MajestikCodeVisitor(CodeBuilder cb) {
    this.cb = cb;
  }

  @Override
  protected void walkPreString(AstNode node) {
    String quotedString = node.getTokenValue();
    String pureString = normalizeString(quotedString);

    this.cb.invokedynamic(
        DynamicCallSiteDesc.of(
            ConstantDescs.BSM_STRING_BUILDER, "string", ConstantDescs.MTD_Object, pureString));
  }

  @Override
  protected void walkPreNumber(AstNode node) {
    var numberString = node.getTokenValue();
    try {
      var number = NumberFormat.getInstance(Locale.ROOT).parse(numberString);
      if (number instanceof Long n) {
        this.cb.loadConstant(n);
        this.cb.invokestatic(ConstantDescs.CD_Long, "valueOf", ConstantDescs.MTD_Longlong);
      } else if (number instanceof Double n) {
        this.cb.loadConstant(n);
        this.cb.invokestatic(ConstantDescs.CD_Double, "valueOf", ConstantDescs.MTD_Doubledouble);
      }
    } catch (ParseException e) {
      LOGGER.log(Level.ERROR, () -> String.format("Error parsing number: %s", numberString));
      throw new RuntimeException("Failed to parse number: " + numberString, e);
    }
  }

  @Override
  protected void walkPreProcedureInvocation(AstNode node) {
    System.out.println("LOG: - Compiling invoke...");

    this.cb.invokedynamic(
        DynamicCallSiteDesc.of(
            ConstantDescs.BSM_GLOBAL_FETCHER,
            "fetch",
            ConstantDescs.MTD_Object,
            "sw",
            node.getParent().getTokenValue()));
  }

  @Override
  protected void walkPostProcedureInvocation(AstNode node) {
    // https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/invoke/CallSite.html

    System.out.println("LOG: - Compiling invoke...");
    this.cb.invokedynamic(
        DynamicCallSiteDesc.of(
            ConstantDescs.BSM_NATURAL_PROC, "()", ConstantDescs.MTD_ObjectObjectObject));
  }

  @Override
  protected void walkPreAtom(AstNode node) {
    var nodeType = ((MagikGrammar) node.getType()).name();
    var nodeText = node.getTokenValue();
    System.out.format("LOG: - Enter %s: %s%n", nodeType, nodeText);
  }

  @Override
  protected void walkPreDefault(AstNode node) {
    var nodeType = ((MagikGrammar) node.getType()).name();
    System.out.format("LOG: - Enter %s%n", nodeType);
  }

  @Override
  protected void walkPostDefault(AstNode node) {
    var nodeType = ((MagikGrammar) node.getType()).name();
    System.out.format("LOG: - Leave %s%n", nodeType);
  }

  /*

  @Override
  public Void visitVar(MajestikParser.VarContext ctx) {
  	if (ctx.parent instanceof MajestikParser.ArgumentContext) {
  		var varname = ctx.getText();
  		this.cb.aload(this.varMap.getOrDefault(varname, -1));
  	}
  	return visitChildren(ctx);
  }

  @Override
  public Void visitAssign(MajestikParser.AssignContext ctx) {
  	var result = visitChildren(ctx);

  	var varname = ctx.lhs().var().getText();

  	if (!this.varMap.containsKey(varname))
  		this.varMap.put(varname, this.varMap.size());

  	this.cb.astore(this.varMap.getOrDefault(varname, -1));
  	return result;
  } */

  private String normalizeString(String quotedString) {
    assert quotedString.length() >= 2 : "String length is too short to be valid.";
    assert (quotedString.charAt(0) == '\"'
                && quotedString.charAt(quotedString.length() - 1) == '\"')
            || (quotedString.charAt(0) == '\''
                && quotedString.charAt(quotedString.length() - 1) == '\'')
        : "String is not properly quoted.";
    return quotedString.substring(1, quotedString.length() - 1);
  }
}

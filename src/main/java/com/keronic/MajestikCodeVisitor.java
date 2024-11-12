package com.keronic;

import module java.base;
import java.lang.System.Logger.Level;
import com.keronic.antlr4.MajestikBaseVisitor;
import com.keronic.antlr4.MajestikParser;
import com.keronic.majestik.ast.CompoundNode;
import com.keronic.majestik.ast.Node;
import com.keronic.majestik.ast.StringNode;
import com.keronic.majestik.constant.ConstantDescs;

public class MajestikCodeVisitor extends MajestikBaseVisitor<Node> {
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
  protected Node defaultResult() {
    LOGGER.log(Level.INFO, () -> "CALLED!");
    return super.defaultResult();
//    return null;
	}

  @Override
  protected Node aggregateResult(Node aggregate, Node nextResult) {
    LOGGER.log(Level.INFO, () -> "CALLED!");
    if (nextResult != null) {
      if (aggregate == null)
        aggregate = new CompoundNode();
      return new CompoundNode((CompoundNode) aggregate, nextResult);
    }
    return aggregate;
	}

  private String normalizeString(String quotedString) {
    assert quotedString.length() >= 2 : "String length is too short to be valid.";
    assert (quotedString.charAt(0) == '\"'
        && quotedString.charAt(quotedString.length() - 1) == '\"')
        || (quotedString.charAt(0) == '\'' && quotedString
            .charAt(quotedString.length() - 1) == '\'') : "String is not properly quoted.";
    return quotedString.substring(1, quotedString.length() - 1);
  }

  @Override
  public Node visitAssign(MajestikParser.AssignContext ctx) {
    LOGGER.log(Level.INFO, () -> "enter visitAssign");
    var result = visitChildren(ctx);

    var varname = ctx.lhs().var().getText();

    if (!this.varMap.containsKey(varname))
      this.varMap.put(varname, this.varMap.size());

    this.cb.astore(this.varMap.getOrDefault(varname, -1));

    LOGGER.log(Level.INFO, () -> "exit visitAssign");

    return result;
  }

  @Override
  public Node visitBlock(MajestikParser.BlockContext ctx) {
    LOGGER.log(Level.INFO, () -> "enter visitBlock");
    var result = visitChildren(ctx);
    LOGGER.log(Level.INFO, () -> result.toString());

    LOGGER.log(Level.INFO, () -> "exit visitBlock");
    return result;
  }

  @Override
  public Node visitInvoke(MajestikParser.InvokeContext ctx) {
    LOGGER.log(Level.INFO, () -> "enter visitInvoke");
    this.cb.invokedynamic(DynamicCallSiteDesc.of(ConstantDescs.BSM_GLOBAL_FETCHER, "fetch",
        ConstantDescs.MTD_Object, "sw", ctx.name.getText()));

    var result = visitChildren(ctx);

    // https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/invoke/CallSite.html

    System.out.println("LOG: - Compiling invoke...");
    this.cb.invokedynamic(DynamicCallSiteDesc.of(ConstantDescs.BSM_NATURAL_PROC, "()",
        ConstantDescs.MTD_ObjectObjectObject));
    LOGGER.log(Level.INFO, () -> "enter visitInvoke");

    return result;
  }

  @Override
  public Node visitNumber(MajestikParser.NumberContext ctx) {
    LOGGER.log(Level.INFO, () -> "enter visitNumber");

    var numberString = ctx.getText();
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
      System.err.println("Error parsing number: " + numberString);
      throw new RuntimeException("Failed to parse number: " + numberString, e);
    }

    var result = visitChildren(ctx);
    LOGGER.log(Level.INFO, () -> "exit visitNumber");

    return result;
  }

  @Override
  public Node visitString(MajestikParser.StringContext ctx) {
    assert ctx.getChildCount() == 1;

    LOGGER.log(Level.INFO, () -> "enter visitString");
    String quotedString = ctx.getText();
    String pureString = normalizeString(quotedString);

    var result = new StringNode(pureString);
    result.compileInto(cb);

    LOGGER.log(Level.INFO, () -> "exit visitString");
    return result;
  }

  @Override
  public Node visitVar(MajestikParser.VarContext ctx) {
    LOGGER.log(Level.INFO, () -> "enter visitVar");
    if (ctx.parent instanceof MajestikParser.ArgumentContext) {
      var varname = ctx.getText();
      this.cb.aload(this.varMap.getOrDefault(varname, -1));
    }
    LOGGER.log(Level.INFO, () -> "exit visitVar");
    return visitChildren(ctx);
  }

}

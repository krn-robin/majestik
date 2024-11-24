package com.keronic.majestik.ast;

import module java.base;

import com.sonar.sslr.api.AstNode;
import java.lang.System.Logger.Level;

public class MajestikCodeVisitor extends MajestikAbstractVisitor<Node> {
  private static final System.Logger LOGGER =
      System.getLogger(MethodHandles.lookup().lookupClass().getName());

  final Map<String, Integer> varMap = new ConcurrentHashMap<>();
  Node currentNode = new CompoundNode();

  /**
   * Constructs a new MajestikCodeVisitor that utilizes a provided CodeBuilder. The CodeBuilder is
   * essential for dynamically building and compiling code during the AST traversal. This design
   * allows for flexible manipulation of code elements at runtime, which is critical for the
   * functionalities implemented in the visit methods.
   *
   * @param cb The CodeBuilder instance that will be used for generating dynamic code constructs.
   */
  public MajestikCodeVisitor() {}

  @Override
  protected Node visitAssignmentExpression(final AstNode node) {
    var result = super.visitAssignmentExpression(node);
    var varname = node.getTokenValue();

    if (!this.varMap.containsKey(varname)) this.varMap.put(varname, this.varMap.size());

    // this.cb.astore(this.varMap.getOrDefault(varname, -1));
    return result;
  }

  @Override
  protected Node visitIdentifier(final AstNode node) {
    var varname = node.getTokenValue();
    var result = new IdentifierNode(varname);

    var varidx = this.varMap.get(varname);
    /*
     * if (varidx == null) this.cb.invokedynamic( DynamicCallSiteDesc.of(
     * ConstantDescs.BSM_GLOBAL_FETCHER, "fetch", ConstantDescs.MTD_Object, "sw",
     * node.getParent().getTokenValue())); else this.cb.aload(varidx);
     */
    return super.visitIdentifier(node);
  }

  @Override
  protected Node visitMagik(AstNode node) {
    LOGGER.log(
        Level.TRACE,
        () -> String.format("Visiting Magik node: %s %s", node.getType(), node.getTokenValue()));
    Node compound = new CompoundNode();
    for (final AstNode childNode : node.getChildren()) {
      compound = this.mergeResults(compound, this.visit(childNode));
    }
    return compound;
  }

  @Override
  protected Node visitNumber(AstNode node) {
    var numberString = node.getTokenValue();
    try {
      var number = NumberFormat.getInstance(Locale.ROOT).parse(numberString);
      var result = new NumberNode(number);
      // result.compileInto(cb);
      return super.visitNumber(node);
    } catch (ParseException e) {
      LOGGER.log(Level.ERROR, () -> String.format("Error parsing number: %s", numberString));
      throw new RuntimeException("Failed to parse number: " + numberString, e);
    }
  }

  @Override
  protected Node visitProcedureInvocation(AstNode node) {
    // https://docs.oracle.com/en/java/javase/22/docs/api/java.base/java/lang/invoke/CallSite.html

    var result = super.visitProcedureInvocation(node);
    /*
     * this.cb.invokedynamic( DynamicCallSiteDesc.of( ConstantDescs.BSM_NATURAL_PROC, "()",
     * ConstantDescs.MTD_ObjectObjectObject));
     */
    return result;
  }

  @Override
  protected Node visitString(AstNode node) {
    LOGGER.log(
        Level.TRACE,
        () -> String.format("Visiting String node: %s %s", node.getType(), node.getTokenValue()));
    String quotedString = node.getTokenValue();
    String pureString = normalizeString(quotedString);

    var result = new StringNode(pureString);
    // result.compileInto(cb);
    return result;
  }

  @Override
  protected Node mergeResults(Node first, Node second) {
    if (second == null) return first;
    if (first == null) return second;

    return switch (first) {
      case CompoundNode c -> new CompoundNode(c, second);
      case Node n -> new CompoundNode(new CompoundNode(first), second);
    };
  }

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

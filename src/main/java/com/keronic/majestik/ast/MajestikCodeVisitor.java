package com.keronic.majestik.ast;

import module java.base;

import com.sonar.sslr.api.AstNode;
import java.lang.System.Logger.Level;
import nl.ramsolutions.sw.magik.api.MagikGrammar;

public class MajestikCodeVisitor extends MajestikAbstractVisitor<Node> {
  private static final System.Logger LOGGER =
      System.getLogger(MethodHandles.lookup().lookupClass().getName());
  private static final String ALLOWED_QUOTES = "\"'";

  final Map<String, Integer> varMap = new ConcurrentHashMap<>();

  /** Constructs a new MajestikCodeVisitor. */
  public MajestikCodeVisitor() {}

  @Override
  protected Node visitAssignmentExpression(final AstNode node) {
    var varname = node.getChildren().getFirst().getTokenValue();
    if (!this.varMap.containsKey(varname)) this.varMap.put(varname, this.varMap.size());
    var varidx = this.varMap.get(varname);

    var lhs = new CompoundNode(new VariableNode(varidx));
    var rhs = new CompoundNode(this.visit(node.getChildren().getLast()));

    return new AssignmentNode(lhs, rhs);
  }

  @Override
  protected Node visitFalse(final AstNode node) {
    return new BooleanNode(false);
  }

  @Override
  protected Node visitIdentifier(final AstNode node) {
    var varname = node.getTokenValue();
    var varidx = this.varMap.get(varname);
    return varidx == null ? new IdentifierNode(varname) : new VariableNode(varidx);
  }

  @Override
  protected Node visitIfExpression(final AstNode node) {
    var expression = this.visit(node.getFirstChild(MagikGrammar.CONDITIONAL_EXPRESSION));

    var ifbody = this.visit(node.getFirstChild(MagikGrammar.BODY));
    var elsebody = this.visit(node.getFirstChild(MagikGrammar.ELSE));

    return new IfExpressionNode(expression, ifbody, elsebody);
  }

  @Override
  protected Node visitMagik(final AstNode node) {
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
  protected Node visitNumber(final AstNode node) {
    LOGGER.log(
        Level.TRACE,
        () -> String.format("Visiting Number node: %s %s", node.getType(), node.getTokenValue()));
    var numberString = node.getTokenValue();
    try {
      var number = NumberFormat.getInstance(Locale.ROOT).parse(numberString);
      return new NumberNode(number);
    } catch (ParseException e) {
      LOGGER.log(
          Level.ERROR,
          () -> String.format("Error parsing number '%s': %s", numberString, e.getMessage()));
      throw new RuntimeException("Failed to parse number: " + numberString, e);
    }
  }

  @Override
  protected Node visitProcedureInvocation(final AstNode node) {
    Node compound = new CompoundNode();
    for (final AstNode childNode : node.getChildren()) {
      compound = this.mergeResults(compound, this.visit(childNode));
    }
    return new InvocationNode((CompoundNode) compound);
  }

  @Override
  protected Node visitString(final AstNode node) {
    LOGGER.log(
        Level.TRACE,
        () -> String.format("Visiting String node: %s %s", node.getType(), node.getTokenValue()));
    String quotedString = node.getTokenValue();
    String pureString = normalizeString(quotedString);

    return new StringNode(pureString);
  }

  @Override
  protected Node mergeResults(final Node first, final Node second) {
    if (second == null) return first;
    if (first == null) return second;

    return switch (first) {
      case CompoundNode c -> new CompoundNode(c, second);
      case Node n -> new CompoundNode(n, second);
    };
  }

  private String normalizeString(final String quotedString) {
    if (quotedString.length() < 2)
      throw new IllegalArgumentException("String length is too short to be valid.");

    char firstChar = quotedString.charAt(0);
    char lastChar = quotedString.charAt(quotedString.length() - 1);
    if ((firstChar != lastChar) || (ALLOWED_QUOTES.indexOf(firstChar) == -1))
      throw new IllegalArgumentException("String is not properly quoted.");

    return quotedString.substring(1, quotedString.length() - 1);
  }

  @Override
  protected Node visitTrue(final AstNode node) {
    return new BooleanNode(true);
  }
}

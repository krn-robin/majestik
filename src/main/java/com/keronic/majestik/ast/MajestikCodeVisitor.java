package com.keronic.majestik.ast;

import module java.base;

import com.sonar.sslr.api.AstNode;
import java.lang.System.Logger.Level;
import nl.ramsolutions.sw.magik.api.MagikGrammar;

public class MajestikCodeVisitor extends MajestikAbstractVisitor<Node> {
  private static final System.Logger LOGGER =
      System.getLogger(MethodHandles.lookup().lookupClass().getName());
  private static final String ALLOWED_QUOTES = "\"'";

  final Map<String, Integer> varMap;

  /** Constructs a new MajestikCodeVisitor. */
  public MajestikCodeVisitor() {
    this(Collections.emptyMap());
  }

  private MajestikCodeVisitor(final Map<String, Integer> variableMap) {
    this.varMap = new ConcurrentHashMap<>(variableMap);
  }

  @Override
  protected Node visitAdditiveExpression(final AstNode node) {
    var lhs = this.visit(node.getChildren().getFirst());
    var rhs = this.visit(node.getChildren().getLast());
    return new AdditiveExpressionNode(lhs, rhs);
  }

  @Override
  protected Node visitAssignmentExpression(final AstNode node) {
    var varname = node.getChildren().getFirst().getTokenValue();
    if (!this.varMap.containsKey(varname)) this.varMap.put(varname, this.varMap.size());
    var varidx = this.varMap.get(varname);

    var lhs = new VariableNode(varidx);
    var rhs = this.visit(node.getChildren().getLast());

    return new AssignmentNode(lhs, rhs);
  }

  @Override
  protected Node visitBlock(final AstNode node) {
    var sub = new MajestikCodeVisitor(this.varMap);
    var body = sub.visit(node.getFirstChild(MagikGrammar.BODY));
    return switch (body) {
      case null -> new BlockNode();
      case CompoundNode n -> new BlockNode(n);
      case Node n -> new BlockNode(n);
    };
  }

  @Override
  protected Node visitCharacter(final AstNode node) {
    LOGGER.log(
        Level.TRACE,
        () ->
            String.format("Visiting Character node: %s %s", node.getType(), node.getTokenValue()));

    String charString = node.getTokenValue();
    char pureCharacter = this.normalizeCharacter(charString);
    return new CharacterNode(pureCharacter);
  }

  @Override
  protected Node visitEqualityExpression(final AstNode node) {
    var lhs = this.visit(node.getChildren().getFirst());
    var rhs = this.visit(node.getChildren().getLast());
    var operator = node.getChildren().get(1).getTokenValue();

    if (!"_is".equals(operator))
      throw new UnsupportedOperationException(
          String.format("Not implemented: operator %s ", operator));

    return new IdentityExpressionNode(lhs, rhs);
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

    var elsechild = node.getFirstChild(MagikGrammar.ELSE);
    var elsebody = elsechild != null ? this.visit(elsechild) : null;

    return new IfExpressionNode(expression, ifbody, elsebody);
  }

  @Override
  protected Node visitLeaveExpression(AstNode node) {
    var label = node.getFirstChild(MagikGrammar.LABEL);
    return switch (label) {
      case AstNode n -> new LeaveNode(n.getLastToken().getValue());
      case null -> LeaveNode.unnamed;
    };
  }

  @Override
  protected Node visitLoopExpression(final AstNode node) {
    var sub = new MajestikCodeVisitor(this.varMap);
    var label = node.getFirstChild(MagikGrammar.LABEL);
    var labeltext =
        switch (label) {
          case AstNode n -> n.getLastToken().getValue();
          case null -> "loop_" + node.hashCode();
        };
    var body = sub.visit(node.getFirstChild(MagikGrammar.BODY));
    var result =
        switch (body) {
          case null -> new LoopNode(labeltext);
          case CompoundNode n -> new LoopNode(labeltext, n);
          case Node n -> new LoopNode(labeltext, n);
        };
    return result;
  }

  @Override
  protected Node visitMagik(final AstNode node) {
    LOGGER.log(
        Level.TRACE,
        () -> String.format("Visiting Magik node: %s %s", node.getType(), node.getTokenValue()));
    Node compound = new ExpressionListNode();
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
    var nodes = node.getChildren().stream().map(this::visit).toArray(Node[]::new);
    return new InvocationNode(new CompoundNode(nodes));
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
  protected Node visitTrue(final AstNode node) {
    return new BooleanNode(true);
  }

  @Override
  protected Node mergeResults(final Node first, final Node second) {
    if (second == null) return first;
    if (first == null) return second;

    return switch (first) {
      case ExpressionListNode c -> new ExpressionListNode(c, second);
      case Node n -> new ExpressionListNode(n, second);
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

  private char normalizeCharacter(final String characterString) {
    if (characterString.length() < 2 || characterString.charAt(0) != '%')
      throw new IllegalArgumentException("String length is too short to be valid.");

    if (characterString.length() > 2)
      throw new UnsupportedOperationException(
          String.format("Not implemented: character %s ", characterString));

    return characterString.charAt(1);
  }
}

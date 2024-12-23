package com.keronic.majestik.ast;

import module java.base;

import com.sonar.sslr.api.AstNode;
import nl.ramsolutions.sw.magik.MagikFile;
import nl.ramsolutions.sw.magik.api.MagikGrammar;

/**
 * Abstract base class for visiting Magik AST nodes.
 *
 * <p>This class maintains state during traversal and is not thread-safe. Each instance should only
 * be used for scanning a single file at a time.
 *
 * @param <T> The type of result produced by this visitor
 */
public abstract class MajestikAbstractVisitor<T> {
  private AstNode top;

  /**
   * Initiates AST traversal for the given Magik file.
   *
   * @param scannedMagikFile The Magik file to scan
   * @return The result of visiting the file's AST
   * @throws IllegalArgumentException if scannedMagikFile is null
   */
  public T scanFile(final MagikFile scannedMagikFile) {
    this.top = Objects.requireNonNull(scannedMagikFile).getTopNode();
    return this.visit(this.top);
  }

  protected T visit(final AstNode node) {
    final var nodeType = Objects.requireNonNull(node).getType();
    if (nodeType instanceof MagikGrammar type) {
      return switch (type) {
        case ADDITIVE_EXPRESSION -> visitAdditiveExpression(node);
        case ASSIGNMENT_EXPRESSION -> visitAssignmentExpression(node);
        case FALSE -> visitFalse(node);
        case IDENTIFIER -> visitIdentifier(node);
        case IF -> visitIfExpression(node);
        case MAGIK -> visitMagik(node);
        case NUMBER -> visitNumber(node);
        case PROCEDURE_INVOCATION -> visitProcedureInvocation(node);
        case STRING -> visitString(node);
        case TRUE -> visitTrue(node);
        default -> visitDefault(node);
      };
    }
    return null;
  }

  /**
   * Default implementation for visiting nodes. Visits all child nodes and merges their results.
   *
   * @param node the AST node to visit
   * @return the merged result of visiting all child nodes
   * @throws NullPointerException if node is null
   */
  protected T visitDefault(final AstNode node) {
    T result = null;
    for (final AstNode childNode : Objects.requireNonNull(node).getChildren()) {
      result = this.mergeResults(result, this.visit(childNode));
    }
    return result;
  }

  /**
   * Merges two results from visiting AST nodes. By default, prefers the second result if it is not
   * null.
   *
   * @param first The result from the first node
   * @param second The result from the second node
   * @return The merged result
   */
  protected T mergeResults(final T first, final T second) {
    return second != null ? second : first;
  }

  /**
   * Visits an assignment expression node.
   *
   * @param node the AST node representing the assignment expression
   * @return result of the visit operation
   */
  protected T visitAssignmentExpression(final AstNode node) {
    return this.visitDefault(node);
  }

  protected T visitAdditiveExpression(final AstNode node) {
    return this.visitDefault(node);
  }

  /**
   * Visits a false literal node.
   *
   * @param node the AST node representing the false literal
   * @return result of the visit operation
   */
  protected T visitFalse(final AstNode node) {
    return this.visitDefault(node);
  }

  /**
   * Visits an identifier node.
   *
   * @param node the AST node representing the identifier
   * @return result of the visit operation
   */
  protected T visitIdentifier(final AstNode node) {
    return this.visitDefault(node);
  }

  /**
   * Visits an if expression node.
   *
   * @param node the AST node representing the if expression
   * @return result of the visit operation
   */
  protected T visitIfExpression(final AstNode node) {
    return this.visitDefault(node);
  }

  protected T visitMagik(final AstNode node) {
    return this.visitDefault(node);
  }

  protected T visitNumber(final AstNode node) {
    return this.visitDefault(node);
  }

  protected T visitProcedureInvocation(final AstNode node) {
    return this.visitDefault(node);
  }

  protected T visitString(final AstNode node) {
    return this.visitDefault(node);
  }

  /**
   * Visits a true literal node.
   *
   * @param node the AST node representing the true literal
   * @return result of the visit operation
   */
  protected T visitTrue(final AstNode node) {
    return this.visitDefault(node);
  }
}

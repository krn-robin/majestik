package com.keronic.majestik.ast;

import module java.base;

import com.sonar.sslr.api.AstNode;
import nl.ramsolutions.sw.magik.MagikFile;
import nl.ramsolutions.sw.magik.api.MagikGrammar;

/** Magik visitor. */
public abstract class MajestikAbstractVisitor<T> {
  private static final System.Logger LOGGER =
      System.getLogger(MethodHandles.lookup().lookupClass().getName());

  private MagikFile magikFile;

  public T scanFile(final MagikFile scannedMagikFile) {
    this.magikFile = scannedMagikFile;

    final AstNode topNode = this.magikFile.getTopNode();
    return this.visit(topNode);
  }

  protected T visit(AstNode node) {
    final var nodeType = node.getType();
    if (nodeType instanceof MagikGrammar type) {
      return switch (type) {
        case ASSIGNMENT_EXPRESSION -> visitAssignmentExpression(node);
        case IDENTIFIER -> visitIdentifier(node);
        case MAGIK -> visitMagik(node);
        case NUMBER -> visitNumber(node);
        case PROCEDURE_INVOCATION -> visitProcedureInvocation(node);
        case STRING -> visitString(node);
        default -> visitDefault(node);
      };
    } else return null;
  }

  protected T visitDefault(AstNode node) {
    T result = null;
    for (final AstNode childNode : node.getChildren()) {
      result = this.mergeResults(result, this.visit(childNode));
    }
    return result;
  }

  protected T mergeResults(T first, T second) {
    return second != null ? second : first;
  }

  protected T visitAssignmentExpression(final AstNode node) {
    return this.visitDefault(node);
  }

  protected T visitIdentifier(AstNode node) {
    return this.visitDefault(node);
  }

  protected T visitMagik(AstNode node) {
    return this.visitDefault(node);
  }

  protected T visitNumber(AstNode node) {
    return this.visitDefault(node);
  }

  protected T visitProcedureInvocation(AstNode node) {
    return this.visitDefault(node);
  }

  protected T visitString(AstNode node) {
    return this.visitDefault(node);
  }
}

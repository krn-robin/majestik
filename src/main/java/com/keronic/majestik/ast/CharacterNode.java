package com.keronic.majestik.ast;

import module java.base;

import com.keronic.majestik.constant.ConstantDescs;

public class CharacterNode extends Node {
  private final char value;

  /**
   * Creates a new CharacterNode with the specified value.
   *
   * @param value the character value to be represented by this node
   */
  public CharacterNode(char value) {
    this.value = value;
  }

  @Override
  public boolean equals(Object obj) {
    return switch (obj) {
      case null -> false;
      case CharacterNode other -> Objects.equals(this.value, other.value);
      default -> false;
    };
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return String.format("CharacterNode{value='%s'}", value);
  }

  @Override
  protected void doCompileInto(final CompilationContext cc) {
    final var cb = cc.getCodeBuilder();
    cb.loadConstant((int) value);
    cb.invokestatic(ConstantDescs.CD_Character, "valueOf", ConstantDescs.MTD_Characterchar);
  }
}

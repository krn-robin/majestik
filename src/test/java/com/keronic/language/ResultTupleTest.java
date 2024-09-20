package com.keronic.language;

import static org.junit.jupiter.api.Assertions.*;

import com.keronic.majestik.language.ResultTuple;
import org.junit.jupiter.api.Test;

class ResultTupleTest {
  private ResultTuple rs1 = ResultTuple.create("Str", "ing");
  private ResultTuple rs2 = ResultTuple.create("Str", "ing");
  private Object[] elems = new Object[] {"Str", "ing"};

  @Test
  void testEquals() throws Throwable {
    assertEquals(rs1, rs2);
    assertNotEquals(rs1, elems);
  }

  @Test
  void testHashcode() throws Throwable {
    assertEquals(rs1.hashCode(), rs2.hashCode());
    assertNotEquals(rs1.hashCode(), elems.hashCode());
  }

  @Test
  void testEmptyTuple() throws Throwable {
    assertSame(ResultTuple.create(), ResultTuple.create());
  }
}

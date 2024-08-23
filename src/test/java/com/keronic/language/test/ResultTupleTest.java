package com.keronic.language.test;

import static org.junit.Assert.*;

import com.keronic.majestik.language.ResultTuple;
import org.junit.Test;

public class ResultTupleTest {
  private ResultTuple rs1 = ResultTuple.create("Str", "ing");
  private ResultTuple rs2 = ResultTuple.create("Str", "ing");
  private Object[] elems = new Object[] {"Str", "ing"};

  @Test
  public void testEquals() throws Throwable {
    assertEquals(rs1, rs2);
    assertNotEquals(rs1, elems);
  }

  @Test
  public void testHashcode() throws Throwable {
    assertEquals(rs1.hashCode(), rs2.hashCode());
    assertNotEquals(rs1.hashCode(), elems.hashCode());
  }

  @Test
  public void testEmptyTuple() throws Throwable {
    assertSame(ResultTuple.create(), ResultTuple.create());
  }
}

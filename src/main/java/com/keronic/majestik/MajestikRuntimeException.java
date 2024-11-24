package com.keronic.majestik;

public class MajestikRuntimeException extends RuntimeException {
    public MajestikRuntimeException(Throwable err) {
        super(err);
    }

  public MajestikRuntimeException(String message) {
    super(message);
  }
}

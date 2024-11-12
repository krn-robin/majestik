package com.keronic.majestik;

public class MajestikRuntimeException extends RuntimeException {
  /**
   * Constructs a new runtime exception with the specified cause.
   *
   * @param cause the cause of the exception
   */
  public MajestikRuntimeException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new runtime exception with the specified detail message.
   *
   * @param message the detail message
   */
  public MajestikRuntimeException(String message) {
    super(message);
  }

  /**
   * Constructs a new runtime exception with the specified detail message and cause.
   *
   * @param message the detail message
   * @param cause the cause of the exception
   */
  public MajestikRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}

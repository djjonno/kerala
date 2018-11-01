package org.elkd.core;

public class ElkdRuntimeException extends RuntimeException {
  public ElkdRuntimeException() {
    super();
  }

  public ElkdRuntimeException(String message) {
    super(message);
  }

  public ElkdRuntimeException(String message, Throwable cause) {
    super(message, cause);
  }
}

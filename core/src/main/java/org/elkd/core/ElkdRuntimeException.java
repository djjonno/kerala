package org.elkd.core;

public class ElkdRuntimeException extends RuntimeException {
  public ElkdRuntimeException() {
    super();
  }

  public ElkdRuntimeException(final String message) {
    super(message);
  }

  public ElkdRuntimeException(final String message, final Throwable cause) {
    super(message, cause);
  }
}

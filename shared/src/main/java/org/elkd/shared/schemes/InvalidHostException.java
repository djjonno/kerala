package org.elkd.shared.schemes;

public class InvalidHostException extends RuntimeException {
  public InvalidHostException(final String host, final Throwable e) {
    super(host + " is not a valid host", e);
  }
}

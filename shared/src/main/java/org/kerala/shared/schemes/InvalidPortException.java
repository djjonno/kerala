package org.kerala.shared.schemes;

public class InvalidPortException extends RuntimeException {
  InvalidPortException(final String port) {
    super(port + " is not a valid port");
  }
}

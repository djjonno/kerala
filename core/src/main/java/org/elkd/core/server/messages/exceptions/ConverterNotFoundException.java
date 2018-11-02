package org.elkd.core.server.messages.exceptions;


public class ConverterNotFoundException extends ConverterException {
  public ConverterNotFoundException(final Class klass) {
    super("Adapter not found for type:" + klass.getCanonicalName());
  }
}

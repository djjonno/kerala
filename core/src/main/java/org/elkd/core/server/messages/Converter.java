package org.elkd.core.server.messages;

import org.elkd.core.server.messages.exceptions.ConverterException;

public interface Converter {
  <T> T convert(Object source) throws ConverterException;
}

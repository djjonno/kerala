package org.elkd.core.server.converters;

import org.elkd.core.server.converters.exceptions.ConverterException;

public interface Converter {
  <T> T convert(Object source) throws ConverterException;
}

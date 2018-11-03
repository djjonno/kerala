package org.elkd.core.server.converters;

import org.elkd.core.server.converters.exceptions.ConverterException;

import java.util.Set;

public interface Converter {
  Set<Class> forTypes();
  <T> T convert(Object source, ConverterRegistry registry) throws ConverterException;
}

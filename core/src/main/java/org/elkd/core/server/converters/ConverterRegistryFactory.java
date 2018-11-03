package org.elkd.core.server.converters;

import com.google.common.collect.ImmutableMap;

class ConverterRegistryFactory {
  private ConverterRegistryFactory() {
  }

  static ImmutableMap<Class, Converter> getConverters() {
    return new ImmutableMap.Builder<Class, Converter>()
        /* Register default converters here */

        .build();
  }
}

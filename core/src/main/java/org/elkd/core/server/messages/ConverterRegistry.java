package org.elkd.core.server.messages;

import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;
import org.elkd.core.server.messages.exceptions.ConverterException;
import org.elkd.core.server.messages.exceptions.ConverterNotFoundException;

import java.util.Map;

public class ConverterRegistry {
  private static final Logger LOG = Logger.getLogger(ConverterRegistry.class);

  private final Map<Class, Converter> mRegistry;

  public ConverterRegistry() {
    this(ConverterRegistryFactory.getConverters());
  }

  @VisibleForTesting
  ConverterRegistry(final Map<Class, Converter> registry) {
    mRegistry = com.google.common.base.Preconditions.checkNotNull(registry, "registry");

    LOG.debug("registered converters");
    for (final Map.Entry<Class, Converter> entry : mRegistry.entrySet()) {
      LOG.debug(entry.toString());
    }
  }

  public <T> T transform(final Object source) throws ConverterException {
    if (!mRegistry.containsKey(source.getClass())) {
      throw new ConverterNotFoundException(source.getClass());
    }

    return mRegistry.get(source.getClass()).convert(source);
  }
}

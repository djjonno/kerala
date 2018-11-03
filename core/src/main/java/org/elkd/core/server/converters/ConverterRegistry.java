package org.elkd.core.server.converters;

import com.google.common.annotations.VisibleForTesting;
import org.apache.log4j.Logger;
import org.elkd.core.server.converters.exceptions.ConverterException;
import org.elkd.core.server.converters.exceptions.ConverterNotFoundException;

import java.util.Map;

public class ConverterRegistry {
  private static final Logger LOG = Logger.getLogger(ConverterRegistry.class);

  /*
   * Registry maps a Class type to its Converter. The converter will convert it
   * to its correct couterpart.
   *
   * For example,
   * RpcAppendEntriesRequest will be converted to AppendEntriesRequest and visa-versa;
   * Each type has its own respective converter.
   */
  private final Map<Class, Converter> mRegistry;

  public ConverterRegistry() {
    this(ConverterRegistryFactory.getConverters());
  }

  @VisibleForTesting
  ConverterRegistry(final Map<Class, Converter> registry) {
    mRegistry = com.google.common.base.Preconditions.checkNotNull(registry, "registry");

    LOG.info("registered converters");
    for (final Map.Entry<Class, Converter> entry : mRegistry.entrySet()) {
      LOG.info(entry.toString());
    }
  }

  public <T> T convert(final Object source) throws ConverterException {
    if (!mRegistry.containsKey(source.getClass())) {
      throw new ConverterNotFoundException(source.getClass());
    }

    return mRegistry.get(source.getClass()).convert(source);
  }
}

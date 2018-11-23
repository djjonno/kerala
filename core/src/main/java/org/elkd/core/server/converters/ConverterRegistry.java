package org.elkd.core.server.converters;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.elkd.core.server.converters.exceptions.ConverterException;
import org.elkd.core.server.converters.exceptions.ConverterNotFoundException;

import java.util.List;
import java.util.Map;

public class ConverterRegistry {
  private static final Logger LOG = Logger.getLogger(ConverterRegistry.class);
  private static ConverterRegistry mInstance;

  /*
   * Registry maps a Class type to its Converter. The converter will convert it
   * to its correct couterpart.
   *
   * For example,
   * RpcAppendEntriesRequest will be converted to AppendEntriesRequest and visa-versa;
   * Each type has its own respective converter.
   */
  private final Map<Class, Converter> mRegistry;

  private ConverterRegistry() {
    this(ConverterRegistryFactory.getConverters());
  }

  @VisibleForTesting
  ConverterRegistry(final List<Converter> converters) {
    Preconditions.checkNotNull(converters, "converters");
    LOG.info("registering converters");

    final ImmutableMap.Builder<Class, Converter> mapBuilder = ImmutableMap.builder();
    for (final Converter converter : converters) {
      for (final Class type : converter.forTypes()) {
        mapBuilder.put(type, converter);
        LOG.debug(type + " -> " + converter);
      }
    }

    mRegistry = mapBuilder.build();
  }

  public <T> T convert(final Object source) throws ConverterException {
    if (!mRegistry.containsKey(source.getClass())) {
      throw new ConverterNotFoundException(source.getClass());
    }

    return mRegistry.get(source.getClass()).convert(source, this);
  }

  public static ConverterRegistry getInstance() {
    synchronized (ConverterRegistry.class) {
      if (mInstance == null) {
        mInstance = new ConverterRegistry();
      }
      return mInstance;
    }
  }
}

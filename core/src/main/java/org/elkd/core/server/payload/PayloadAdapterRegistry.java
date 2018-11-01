package org.elkd.core.server.payload;

import java.util.Map;

public class PayloadAdapterRegistry {

  private final Map<Class, PayloadAdapter> mRegistry;

  public PayloadAdapterRegistry(final Map<Class, PayloadAdapter> registry) {
    mRegistry = registry;
  }

  public <T> T transform(Class<T> targetType, final Object source) throws AdapterNotFoundException {
    if (!mRegistry.containsKey(targetType)) {
      throw new AdapterNotFoundException(targetType);
    }

    return mRegistry.get(targetType).transform(targetType, source);
  }
}

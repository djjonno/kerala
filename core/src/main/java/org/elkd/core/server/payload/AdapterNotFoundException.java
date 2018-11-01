package org.elkd.core.server.payload;

import org.elkd.core.ElkdRuntimeException;

public class AdapterNotFoundException extends ElkdRuntimeException {
  public AdapterNotFoundException(final Class klass) {
    super("Adapter not found for type:" + klass.getCanonicalName());
  }
}

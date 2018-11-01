package org.elkd.core.server.payload;

public interface PayloadAdapter {
  <T> T transform(Class<T> targetType, Object source);
}

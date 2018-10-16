package org.elkd.core.log;

public interface LogCommand<T> {
  LogChangeReason getReason();
  T execute();
}

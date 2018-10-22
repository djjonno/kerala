package org.elkd.core.log.commands;

import org.elkd.core.log.LogChangeReason;

public interface LogCommand<T> {
  LogChangeReason getReason();
  T execute();
}

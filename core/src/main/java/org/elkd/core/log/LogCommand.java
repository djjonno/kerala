package org.elkd.core.log;

import org.elkd.core.log.LogCommandReasons.LogCommandReason;

public interface LogCommand<T> {
  LogCommandReason getReason();
  T execute();
}

package org.elkd.core.log;

import org.elkd.core.log.LogChangeReasons.LogChangeReason;

public interface LogCommand<T> {
  LogChangeReason getReason();
  T execute();
}

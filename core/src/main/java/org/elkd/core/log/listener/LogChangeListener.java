package org.elkd.core.log.listener;

import java.util.List;

public interface LogChangeListener<T> {
  void onCommit(List<T> entries);
  void onAppend(List<T> entries);
}

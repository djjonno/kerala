package org.elkd.core.log;

public interface LogChangeListener<T> {
  void onCommit(T entry);
  void onAppend(T entry);
}

package org.elkd.core.log;

public interface LogChangeListener<E> {
  void onCommit(E entry);
  void onAppend(E entry);
}

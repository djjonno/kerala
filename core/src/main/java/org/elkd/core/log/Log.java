package org.elkd.core.log;

import java.util.List;

public interface Log<T> {
  long append(T object);

  T read(long index);

  List<T> read(long from, long to);

  void commit(long index);

  void revert(long index);
}

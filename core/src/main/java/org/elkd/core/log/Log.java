package org.elkd.core.log;

import java.util.List;

public interface Log<T> {
  long append(T entries);

  long append(List<T> entries);

  T read(long index);

  List<T> read(long from, long to);

  CommitResult<T> commit(long index);

  void revert(long index);
}

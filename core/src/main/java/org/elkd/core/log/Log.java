package org.elkd.core.log;

import java.util.List;

public interface Log<T> {

  long INDEX_FROM = -1;

  long append(T entry);

  long append(long index, T entry);

  T read(long index);

  List<T> read(long from, long to);

  CommitResult<T> commit(long index);

  void revert(long index);

  long getCommitIndex();

  long getLastIndex();
}

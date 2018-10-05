package org.elkd.core.log;

import java.util.List;

public interface Log {
  long append(Entry entry);

  Entry read(long index);

  List<Entry> read(long from, long to);

  void commit(long index);

  void rollback(long index);
}

package org.elkd.core.log;

import java.util.List;

public interface Log {
  void append(Event event);

  Event read(long index);

  List<Event> read(long from, long to);

  void commit(long index);

  void rollback(long index);
}

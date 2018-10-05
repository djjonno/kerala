package org.elkd.core.log;

public class LogCommandReasons {
  public interface LogCommandReason { }

  public enum AppendReason implements LogCommandReason {
    REPLICATION,
    CLIENT
  }

  public enum ReadReason implements LogCommandReason {
    REPLICATION,
    CLIENT
  }

  public enum CommitReason implements LogCommandReason {
    REPLICATION,
    CLIENT
  }
}

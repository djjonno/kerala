package org.elkd.core.log;

public class LogChangeReasons {
  public interface LogChangeReason { }

  public enum AppendReason implements LogChangeReason {
    REPLICATION,
    CLIENT
  }

  public enum ReadReason implements LogChangeReason {
    REPLICATION,
    CLIENT
  }

  public enum CommitReason implements LogChangeReason {
    REPLICATION,
    CLIENT
  }
}

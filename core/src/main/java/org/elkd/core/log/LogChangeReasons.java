package org.elkd.core.log;

public class LogChangeReasons {
  public interface LogChangeReason { }

  public enum AppendReason implements LogChangeReason {
    REPLICATION,
    CLIENT
  }
}

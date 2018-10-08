package org.elkd.core.consensus;

public interface State {
  AppendEntriesResponse handleAppendEntries(AppendEntriesRequest request);
  RequestVotesResponse handleRequestVotes(RequestVotesRequest request);

  void on();
  void off();
}

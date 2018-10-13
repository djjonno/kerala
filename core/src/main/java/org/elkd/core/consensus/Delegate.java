package org.elkd.core.consensus;

public interface Delegate extends State {
  AppendEntriesResponse delegateAppendEntries(AppendEntriesRequest request);
  RequestVotesResponse delegateRequestVotes(RequestVotesRequest request);
}

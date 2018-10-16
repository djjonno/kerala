package org.elkd.core.consensus;

import org.elkd.core.consensus.payload.AppendEntriesRequest;
import org.elkd.core.consensus.payload.AppendEntriesResponse;
import org.elkd.core.consensus.payload.RequestVotesRequest;
import org.elkd.core.consensus.payload.RequestVotesResponse;

public interface Delegate extends State {
  AppendEntriesResponse delegateAppendEntries(AppendEntriesRequest request);
  RequestVotesResponse delegateRequestVotes(RequestVotesRequest request);
}

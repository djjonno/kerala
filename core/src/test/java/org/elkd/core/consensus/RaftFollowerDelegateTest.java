package org.elkd.core.consensus;

import io.grpc.stub.StreamObserver;
import org.elkd.core.config.Config;
import org.elkd.core.consensus.election.ElectionMonitor;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class RaftFollowerDelegateTest {
  private static final int ELECTION_TIMEOUT = 50;
  private static final int TIMEOUT_BUFFER = 10; // allow some lee-way on timeout

  @Mock Config mConfig;
  @Mock Raft mRaft;
  @Mock AppendEntriesRequest mAppendEntriesRequest;
  @Mock StreamObserver<AppendEntriesResponse> mAppendEntriesResponseStreamObserver;
  @Mock RequestVoteRequest mRequestVoteRequest;
  @Mock StreamObserver<RequestVoteResponse> mRequestVoteResponseStreamObserver;
  @Mock ElectionMonitor mElectionMonitor;

  private RaftFollowerDelegate mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    setupCommonExpectations();

    mUnitUnderTest = new RaftFollowerDelegate(mRaft, mElectionMonitor);
  }

  private void setupCommonExpectations() {
    doReturn(mConfig)
        .when(mRaft)
        .getConfig();

    doReturn(ELECTION_TIMEOUT)
        .when(mConfig)
        .getAsInteger(Config.KEY_RAFT_ELECTION_TIMEOUT_MS);
  }

  @Test
  public void should_transition_to_candidateState_if_timeout() throws InterruptedException {
    // Given - builtin electionMonitor
    mUnitUnderTest = new RaftFollowerDelegate(mRaft);

    // When
    mUnitUnderTest.on();

    // Then
    Thread.sleep(ELECTION_TIMEOUT + TIMEOUT_BUFFER);
    verify(mRaft).transitionToState(RaftCandidateDelegate.class);
  }

  @Test
  public void should_cancel_timeout_when_appendEntries_received() throws InterruptedException {
    // Given
    mUnitUnderTest.on();

    // When
    mUnitUnderTest.delegateAppendEntries(mAppendEntriesRequest, mAppendEntriesResponseStreamObserver);

    // Then
    verify(mElectionMonitor, times(2)).reset();
    verify(mRaft, never()).transitionToState(any());
  }

  @Test
  public void should_cancel_timeout_when_requestVote_received() throws InterruptedException {
    // Given
    mUnitUnderTest.on();

    // When
    mUnitUnderTest.delegateRequestVote(mRequestVoteRequest, mRequestVoteResponseStreamObserver);

    // Then
    verify(mElectionMonitor, times(2)).reset();
  }
}

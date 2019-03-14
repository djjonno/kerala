package org.elkd.core.consensus;

import com.google.common.collect.ImmutableList;
import io.grpc.stub.StreamObserver;
import org.elkd.core.config.Config;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.elkd.core.consensus.messages.Entry;
import org.elkd.core.consensus.messages.RequestVoteRequest;
import org.elkd.core.consensus.messages.RequestVoteResponse;
import org.elkd.core.log.Log;
import org.elkd.core.log.LogCommandExecutor;
import org.elkd.core.log.commands.AppendFromCommand;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class RaftFollowerDelegateTest {
  private static final int ELECTION_TIMEOUT = 50;
  private static final int TIMEOUT_BUFFER = 10; // allow some lee-way on timeout
  private static final int CURRENT_TERM = 0;
  private static final String LEADER_ID = "leaderId";
  private static final int LEADER_COMMIT = 0;

  private static final Entry ENTRY_1 = Entry.builder(0, "event1").build();
  private static final Entry ENTRY_2 = Entry.builder(0, "event2").build();
  private static final int INDEX_ENTRY_1 = 0;
  private static final int INDEX_ENTRY_2 = 1;

  @Mock Config mConfig;
  @Mock Raft mRaft;
  @Mock RaftContext mRaftContext;
  @Mock AppendEntriesRequest mAppendEntriesRequest;
  @Mock StreamObserver<AppendEntriesResponse> mAppendEntriesResponseStreamObserver;
  @Mock RequestVoteRequest mRequestVoteRequest;
  @Mock StreamObserver<RequestVoteResponse> mRequestVoteResponseStreamObserver;
  @Mock TimeoutMonitor mTimeoutMonitor;
  @Mock Log<Entry> mLog;

  private LogCommandExecutor<Entry> mLogCommandExecutor;
  private RaftFollowerDelegate mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    setupCommonExpectations();

    mUnitUnderTest = new RaftFollowerDelegate(mRaft, mTimeoutMonitor);
  }

  private void setupCommonExpectations() {
    mLogCommandExecutor = spy(new LogCommandExecutor<>(mLog));
    doReturn(mConfig)
        .when(mRaft)
        .getConfig();
    doReturn(mRaftContext)
        .when(mRaft)
        .getRaftContext();
    doReturn(CURRENT_TERM)
        .when(mRaftContext)
        .getCurrentTerm();

    doReturn(mLogCommandExecutor)
        .when(mRaft)
        .getLogCommandExecutor();
    doReturn(mLog)
        .when(mRaft)
        .getLog();
    doReturn(ENTRY_1)
        .when(mLog)
        .read(INDEX_ENTRY_1);
    doReturn(ENTRY_2)
        .when(mLog)
        .read(INDEX_ENTRY_2);
    doReturn(1L)
        .when(mLog)
        .getLastIndex();

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
    verify(mTimeoutMonitor, times(2)).reset(anyLong());
    verify(mRaft, never()).transitionToState(any());
  }

  @Test
  public void should_cancel_timeout_when_requestVote_received() throws InterruptedException {
    // Given
    mUnitUnderTest.on();

    // When
    mUnitUnderTest.delegateRequestVote(mRequestVoteRequest, mRequestVoteResponseStreamObserver);

    // Then
    verify(mTimeoutMonitor, times(2)).reset(anyLong());
  }

  @Test
  public void raft_spec__should_reply_false_if_term_lt_currentTerm() {
    // Given
    final AppendEntriesRequest request = AppendEntriesRequest.builder(CURRENT_TERM - 1, 1, 0, LEADER_ID, LEADER_COMMIT).build();

    // When
    mUnitUnderTest.delegateAppendEntries(request, mAppendEntriesResponseStreamObserver);

    // Then
    assertFalse(getReply(mAppendEntriesResponseStreamObserver).isSuccessful());
  }

  @Test
  public void raft_spec__should_reply_false_if_no_entry_at_prevLogIndex() {
    // Given
    final int futurePrevLogIndex = 10;
    final AppendEntriesRequest request = AppendEntriesRequest.builder(0, 0, futurePrevLogIndex, LEADER_ID, LEADER_COMMIT).build();

    // When
    mUnitUnderTest.delegateAppendEntries(request, mAppendEntriesResponseStreamObserver);

    // Then
    assertFalse(getReply(mAppendEntriesResponseStreamObserver).isSuccessful());
  }

  @Test
  public void raft_spec__should_reply_false_if_entry_term_at_prevLogIndex_does_not_match_prevLogTerm() {
    // Given
    final AppendEntriesRequest request = AppendEntriesRequest.builder(0, 2, 1, LEADER_ID, LEADER_COMMIT).build();

    // When
    mUnitUnderTest.delegateAppendEntries(request, mAppendEntriesResponseStreamObserver);

    // Then
    assertFalse(getReply(mAppendEntriesResponseStreamObserver).isSuccessful());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void raft_spec__should_append_new_entries() {
    // Given
    final ImmutableList<Entry> entries = ImmutableList.of(
        Entry.builder(0, "first").build(),
        Entry.builder(0, "second").build(),
        Entry.builder(0, "third").build());
    final AppendEntriesRequest request = AppendEntriesRequest.builder(0, 0, 1, LEADER_ID, LEADER_COMMIT)
        .withEntries(entries)
        .build();

    // When
    mUnitUnderTest.delegateAppendEntries(request, mAppendEntriesResponseStreamObserver);

    // Then
    assertTrue(getReply(mAppendEntriesResponseStreamObserver).isSuccessful());
    verify(mLogCommandExecutor).execute(any(AppendFromCommand.class));
    final ArgumentCaptor<Entry> captor = ArgumentCaptor.forClass(Entry.class);
    verify(mLog, times(entries.size())).append(captor.capture());
    assertEquals(captor.getAllValues(), entries);
    verify(mLog, never()).revert(anyLong());
  }

  @Test
  @SuppressWarnings("unchecked")
  public void raft_spec__should_revert_and_overwrite_conflicted_entries() {
    // Given
    final int prevLogIndex = 0;
    final ImmutableList<Entry> entries = ImmutableList.of(
        Entry.builder(1, "newSecond").build(), /* this is the conflict entry */
        Entry.builder(1, "third").build()
    );
    final AppendEntriesRequest request = AppendEntriesRequest.builder(1, 0, prevLogIndex, LEADER_ID, LEADER_COMMIT)
        .withEntries(entries)
        .build();

    // When
    mUnitUnderTest.delegateAppendEntries(request, mAppendEntriesResponseStreamObserver);

    // Then
    assertTrue(getReply(mAppendEntriesResponseStreamObserver).isSuccessful());
    verify(mLogCommandExecutor).execute(any(AppendFromCommand.class));
    final ArgumentCaptor<Entry> captor = ArgumentCaptor.forClass(Entry.class);
    verify(mLog, times(2)).append(captor.capture());
    assertEquals(captor.getAllValues(), entries);
    verify(mLog).revert(prevLogIndex + 1);
  }

  @Test
  public void raft_spec__should_commit_to_leaderCommit() {
    // Given
    final int leaderCommit = 0;
    final AppendEntriesRequest request = AppendEntriesRequest.builder(0, 0, 1, LEADER_ID, leaderCommit).build();
    final long logCommit = -1;
    doReturn(logCommit)
        .when(mLog)
        .getCommitIndex();

    // When
    mUnitUnderTest.delegateAppendEntries(request, mAppendEntriesResponseStreamObserver);

    // Then
    verify(mLog).commit(leaderCommit);
  }

  private AppendEntriesResponse getReply(final StreamObserver<AppendEntriesResponse> streamObserver) {
    final ArgumentCaptor<AppendEntriesResponse> captor = ArgumentCaptor.forClass(AppendEntriesResponse.class);
    verify(streamObserver).onNext(captor.capture());
    return captor.getValue();
  }
}

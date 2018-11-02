package org.elkd.core.consensus.payload;

import com.google.common.collect.ImmutableList;
import org.elkd.core.consensus.messages.AppendEntriesRequest;
import org.elkd.core.consensus.messages.Entry;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class AppendEntriesRequestTest {
  private static final int TERM = 0;
  private static final int PREV_LOG_TERM = 1;
  private static final int LEADER_ID = 2;
  private static final long LEADER_COMMIT = 3L;
  private static final List<Entry> ENTRIES = ImmutableList.of(Entry.builder("event").build());

  @Test
  public void should_build_with_properties() {
    // Given / When
    final AppendEntriesRequest request = AppendEntriesRequest.builder(
        TERM,
        PREV_LOG_TERM,
        LEADER_ID,
        LEADER_COMMIT
    )
        .withEntries(ENTRIES)
        .build();

    // Then
    assertEquals(TERM, request.getTerm());
    assertEquals(PREV_LOG_TERM, request.getPrevLogTerm());
    assertEquals(LEADER_ID, request.getLeaderId());
    assertEquals(LEADER_COMMIT, request.getLeaderCommit());
    assertEquals(ENTRIES, request.getEntries());
  }

  @Test
  public void should_build_with_single_event() {
    // Given / When
    final AppendEntriesRequest request = AppendEntriesRequest.builder(
        TERM,
        PREV_LOG_TERM,
        LEADER_ID,
        LEADER_COMMIT
    )
        .withEntry(ENTRIES.get(0))
        .build();

    // Then
    assertEquals(TERM, request.getTerm());
    assertEquals(PREV_LOG_TERM, request.getPrevLogTerm());
    assertEquals(LEADER_ID, request.getLeaderId());
    assertEquals(LEADER_COMMIT, request.getLeaderCommit());
    assertEquals(ENTRIES, request.getEntries());
  }
}

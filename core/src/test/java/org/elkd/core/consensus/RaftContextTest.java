package org.elkd.core.consensus;

import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertEquals;

public class RaftContextTest {
  private static final int DEFAULT_CURRENT_TERM = 0;
  private static final String DEFAULT_VOTED_FOR = null;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void should_have_default_values() {
    // Given / When
    final RaftContext raftContext = new RaftContext();

    // Then
    assertEquals(DEFAULT_CURRENT_TERM, raftContext.getCurrentTerm());
    assertEquals(DEFAULT_VOTED_FOR, raftContext.getVotedFor());
  }

  @Test
  public void should_set_votedFor() {
    // Given
    final RaftContext raftContext = new RaftContext();
    final String votedFor = "node-id";

    // When
    raftContext.setVotedFor(votedFor);

    // Then
    assertEquals(votedFor, raftContext.getVotedFor());
  }

  @Test
  public void should_set_currentTerm() {
    // Given
    final RaftContext raftContext = new RaftContext();
    final int currentTerm = 10;

    // When
    raftContext.setCurrentTerm(currentTerm);

    // Then
    assertEquals(currentTerm, raftContext.getCurrentTerm());
  }
}

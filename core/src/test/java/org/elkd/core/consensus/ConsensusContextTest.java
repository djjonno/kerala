package org.elkd.core.consensus;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ConsensusContextTest {
  private static final int DEFAULT_CURRENT_TERM = 0;
  private static final Integer DEFAULT_VOTED_FOR = null;

  @Test
  public void should_have_default_values() {
    // Given / When
    final ConsensusContext consensusContext = new ConsensusContext();

    // Then
    assertEquals(DEFAULT_CURRENT_TERM, consensusContext.getCurrentTerm());
    assertEquals(DEFAULT_VOTED_FOR, consensusContext.getVotedFor());
  }

  @Test
  public void should_set_votedFor() {
    // Given
    final ConsensusContext consensusContext = new ConsensusContext();
    final Integer votedFor = 10;

    // When
    consensusContext.setVotedFor(votedFor);

    // Then
    assertEquals(votedFor, consensusContext.getVotedFor());
  }

  @Test
  public void should_set_currentTerm() {
    // Given
    final ConsensusContext consensusContext = new ConsensusContext();
    final int currentTerm = 10;

    // When
    consensusContext.setCurrentTerm(currentTerm);

    // Then
    assertEquals(currentTerm, consensusContext.getCurrentTerm());
  }
}

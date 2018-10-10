package org.elkd.core.consensus;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConsensusContextTest {
  private static final Integer DEFAULT_CURRENT_TERM = 0;
  private static final Integer DEFAULT_VOTED_FOR = null;

  @Test
  public void should_have_default_values() {
    // Given / When
    final ConsensusContext consensusContext = new ConsensusContext();

    // Then
    assertEquals(DEFAULT_CURRENT_TERM, consensusContext.getCurrentTerm());
    assertEquals(DEFAULT_VOTED_FOR, consensusContext.getVotedFor());
  }
}
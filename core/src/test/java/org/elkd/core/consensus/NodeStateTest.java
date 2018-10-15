package org.elkd.core.consensus;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class NodeStateTest {
  private static final int DEFAULT_CURRENT_TERM = 0;
  private static final Integer DEFAULT_VOTED_FOR = null;

  @Test
  public void should_have_default_values() {
    // Given / When
    final NodeState nodeState = new NodeState();

    // Then
    assertEquals(DEFAULT_CURRENT_TERM, nodeState.getCurrentTerm());
    assertEquals(DEFAULT_VOTED_FOR, nodeState.getVotedFor());
  }

  @Test
  public void should_set_votedFor() {
    // Given
    final NodeState nodeState = new NodeState();
    final Integer votedFor = 10;

    // When
    nodeState.setVotedFor(votedFor);

    // Then
    assertEquals(votedFor, nodeState.getVotedFor());
  }

  @Test
  public void should_set_currentTerm() {
    // Given
    final NodeState nodeState = new NodeState();
    final int currentTerm = 10;

    // When
    nodeState.setCurrentTerm(currentTerm);

    // Then
    assertEquals(currentTerm, nodeState.getCurrentTerm());
  }
}

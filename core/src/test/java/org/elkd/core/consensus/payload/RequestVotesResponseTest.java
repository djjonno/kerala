package org.elkd.core.consensus.payload;

import org.elkd.core.consensus.messages.RequestVotesResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class RequestVotesResponseTest {
  private static final int TERM = 0;
  private static final boolean VOTE_GRANTED = true;

  @Test
  public void should_build_with_properties() {
    // Given / When
    final RequestVotesResponse response = RequestVotesResponse.builder(TERM, VOTE_GRANTED).build();

    // Then
    assertEquals(TERM, response.getTerm());
    assertEquals(VOTE_GRANTED, response.isVoteGranted());
  }
}

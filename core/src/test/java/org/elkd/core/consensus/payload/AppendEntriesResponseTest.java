package org.elkd.core.consensus.payload;

import org.elkd.core.consensus.messages.AppendEntriesResponse;
import org.junit.Test;

import static org.junit.Assert.*;

public class AppendEntriesResponseTest {
  private static final int TERM = 0;
  private static final boolean SUCCESS = true;

  @Test
  public void should_build_with_properties() {
    // Given / When
    final AppendEntriesResponse response = AppendEntriesResponse.builder(TERM, SUCCESS).build();

    // Then
    assertEquals(TERM, response.getTerm());
    assertEquals(SUCCESS, response.isSuccessful());
  }
}

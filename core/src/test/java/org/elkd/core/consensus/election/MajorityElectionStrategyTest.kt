package org.elkd.core.consensus.election

import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class MajorityElectionStrategyTest {

  @Mock private lateinit var electionTally: ElectionTally

  private lateinit var electionStrategy: MajorityElectionStrategy

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)
    electionStrategy = MajorityElectionStrategy()
    whenever(electionTally.expectedVotes).thenReturn(EXPECTED_VOTES)
  }

  @Test
  fun should_be_complete_when_atleast_more_than_half_votes_received() {
    // Given
    whenever(electionTally.totalUpVotes()).thenReturn(EXPECTED_VOTES / 2 + 1)

    // When
    val complete = electionStrategy.isComplete(electionTally)

    // Then
    assertTrue(complete)
  }

  @Test
  fun should_be_successful_if_gt_majority_up_voted() {
    // Given

    // When

    // Then
  }

  @Test
  fun should_be_unsuccessful_gt_majority_down_voted() {
    // Given

    // When

    // Then
  }

  companion object {
    private const val EXPECTED_VOTES = 5
  }
}
package org.elkd.core.consensus.states.candidate.election

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
    setupCommonExpectations()
  }

  @Test
  fun should_be_complete_when_at_least_more_than_half_votes_received() {
    // Given
    whenever(electionTally.totalVotes()).thenReturn(majorityVoteCount())

    // When
    val complete = electionStrategy.isComplete(electionTally)

    // Then
    assertTrue(complete)
  }

  @Test
  fun should_be_successful_if_gt_majority_up_voted() {
    // Given
    whenever(electionTally.totalVotes()).thenReturn(majorityVoteCount())
    whenever(electionTally.totalUpVotes()).thenReturn(majorityVoteCount())

    // When
    val complete = electionStrategy.isComplete(electionTally)
    val successful = electionStrategy.isSuccessful(electionTally)

    // Then
    assertTrue(complete)
    assertTrue(successful)
  }

  @Test
  fun should_be_unsuccessful_gt_minority_up_voted() {
    // Given
    whenever(electionTally.totalUpVotes()).thenReturn(minorityVoteCount())

    // When
    val complete = electionStrategy.isComplete(electionTally)
    val successful = electionStrategy.isSuccessful(electionTally)

    // Then
    assertTrue(complete)
    assertFalse(successful)
  }

  private fun setupCommonExpectations() {
    whenever(electionTally.totalVotes()).thenReturn(EXPECTED_VOTES)
  }

  companion object {
    private fun majorityVoteCount() = EXPECTED_VOTES / 2 + 1
    private fun minorityVoteCount() = EXPECTED_VOTES / 2 - 1
    private const val EXPECTED_VOTES = 5
  }
}
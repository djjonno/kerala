package org.elkd.core.consensus.election

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ElectionTallyTest {

  private lateinit var unitUnderTest: ElectionTally

  @Before
  fun setup() {
    unitUnderTest = ElectionTally(EXPECTED_VOTES)
  }

  @Test
  fun should_have_zero_votes() {
    // Given / When - unitUnderTest()

    // Then
    assertEquals(0, unitUnderTest.totalUpVotes())
    assertEquals(0, unitUnderTest.totalDownVotes())
  }

  @Test
  fun should_have_unique_votes() {
    // Given / When
    unitUnderTest.recordUpVote(ID_1)
    unitUnderTest.recordUpVote(ID_1)
    unitUnderTest.recordUpVote(ID_1)
    unitUnderTest.recordUpVote(ID_1)
    unitUnderTest.recordUpVote(ID_1)

    // Then
    assertEquals(1, unitUnderTest.totalUpVotes())
    assertEquals(1, unitUnderTest.totalVotes())
  }

  @Test
  fun should_ignore_voting_up_and_down_from_same_id() {
    // Given / When
    unitUnderTest.recordUpVote(ID_1)
    unitUnderTest.recordDownVote(ID_1)

    // Then
    assertEquals(1, unitUnderTest.totalVotes())
    assertEquals(1, unitUnderTest.totalUpVotes())
    assertEquals(0, unitUnderTest.totalDownVotes())
  }

  @Test
  fun should_return_sum_of_up_down_votes() {
    // Given / When
    val upVotes = listOf(ID_1, ID_2)
    upVotes.forEach { unitUnderTest.recordUpVote(it) }

    val downVotes = listOf(ID_3)
    downVotes.forEach { unitUnderTest.recordDownVote(it) }

    // Then
    assertEquals(upVotes.size, unitUnderTest.totalUpVotes())
    assertEquals(downVotes.size, unitUnderTest.totalDownVotes())
    assertEquals(upVotes.size + downVotes.size, unitUnderTest.totalVotes())
  }

  companion object {
    private const val EXPECTED_VOTES = 5
    private const val ID_1 = "id1";
    private const val ID_2 = "id2";
    private const val ID_3 = "id3";
  }
}

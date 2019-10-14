package org.elkd.core.consensus

import kotlin.test.assertTrue
import org.elkd.core.config.ConfigProvider
import org.elkd.core.consensus.states.RaftStateFactory
import org.elkd.core.consensus.states.State
import org.elkd.core.consensus.states.candidate.RaftCandidateState
import org.elkd.core.consensus.states.follower.RaftFollowerState
import org.elkd.core.consensus.states.leader.RaftLeaderState
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations

class DefaultRaftDelegateFactoryTest {

  @Mock internal lateinit var raft: Raft

  private var unitUnderTest: RaftStateFactory? = null

  @Before
  @Throws(Exception::class)
  fun setup() {
    MockitoAnnotations.initMocks(this)

    unitUnderTest = RaftStateFactory(raft)

    doReturn(ConfigProvider.compileConfig(arrayOf()))
        .`when`(raft)
        .config
  }

  @Test
  fun should_return_correct_states() {
    // Given / When
    val followerState = unitUnderTest?.getState(State.FOLLOWER)
    val candidateState = unitUnderTest?.getState(State.CANDIDATE)
    val leaderState = unitUnderTest?.getState(State.LEADER)

    // Then
    assertTrue { followerState is RaftFollowerState }
    assertTrue { candidateState is RaftCandidateState }
    assertTrue { leaderState is RaftLeaderState }
  }
}

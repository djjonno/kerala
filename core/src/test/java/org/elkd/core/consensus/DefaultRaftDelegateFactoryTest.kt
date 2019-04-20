package org.elkd.core.consensus

import org.elkd.core.config.ConfigProvider
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import org.mockito.Mockito.doReturn
import kotlin.test.assertTrue

class DefaultRaftDelegateFactoryTest {

  @Mock internal lateinit var raft: Raft

  private var unitUnderTest: DefaultStateFactory? = null

  @Before
  @Throws(Exception::class)
  fun setup() {
    MockitoAnnotations.initMocks(this)

    unitUnderTest = DefaultStateFactory(raft)

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

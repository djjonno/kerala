package org.elkd.core.consensus.replication

import com.nhaarman.mockitokotlin2.*
import kotlinx.coroutines.runBlocking
import org.elkd.core.consensus.states.leader.LeaderContext
import org.elkd.core.consensus.Raft
import org.elkd.core.server.cluster.ClusterSet
import org.elkd.core.server.cluster.Node
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ReplicatorTest {

  @Mock lateinit var raft: Raft
  @Mock lateinit var clusterSet: ClusterSet
  @Mock lateinit var leaderContext: LeaderContext
  @Mock lateinit var replicationControllerFactory: ReplicationControllerFactory

  private lateinit var replicator: Replicator

  @Before
  fun setup() {
    MockitoAnnotations.initMocks(this)
    configureCommonExpectations()
    replicator = Replicator(raft, leaderContext, replicationControllerFactory)
  }

  private fun configureCommonExpectations() {
    doReturn(mock<ReplicationController>())
        .whenever(replicationControllerFactory)
        .create(any(), any(), any(), any())
  }

  @Test
  fun should_initialize_workers_for_nodes() = runBlocking {
    // Given
    val nodes = setOf<Node>(mock(), mock(), mock())
    configureClusterSet(nodes)

    // When
    replicator.start()

    // Then
    nodes.forEach { verify(replicationControllerFactory).create(eq(it), eq(leaderContext), eq(raft), any()) }
  }

  private fun configureClusterSet(nodes: Set<Node>) {
    doReturn(clusterSet)
        .whenever(raft)
        .clusterSet
    doReturn(nodes)
        .whenever(clusterSet)
        .nodes
  }
}
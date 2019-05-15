package org.elkd.core.consensus

import com.nhaarman.mockitokotlin2.mock
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.log.LogCommandExecutor
import org.elkd.core.log.LogInvoker
import org.elkd.core.log.LogProvider
import org.elkd.core.server.cluster.ClusterMessenger
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations

class RaftFactoryTest {

  @Mock lateinit var clusterMessenger: ClusterMessenger
  @Mock lateinit var log: LogInvoker<Entry>
  @Mock lateinit var logProvider: LogProvider<Entry>
  @Mock lateinit var logCommandExecutor: LogCommandExecutor<Entry>

  @Before
  @Throws(Exception::class)
  fun setup() {
    MockitoAnnotations.initMocks(this)
    doReturn(log)
        .`when`(logProvider)
        .log
    doReturn(logCommandExecutor)
        .`when`(logProvider)
        .logCommandExecutor
  }

  @Test
  fun should_return_raft_with_properties() {
    // Given / When
    val raft = RaftFactory.create(mock(), logProvider, clusterMessenger)

    // Then
    assertSame(clusterMessenger, raft.clusterMessenger)
    assertSame(log, raft.log)
    assertSame(logCommandExecutor, raft.logCommandExecutor)
  }
}

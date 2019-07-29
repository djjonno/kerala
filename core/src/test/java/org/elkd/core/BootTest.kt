package org.elkd.core

import org.elkd.core.config.Config
import org.elkd.core.consensus.Raft
import org.elkd.core.server.Server
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

import java.io.IOException

import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify

class BootTest {

  @Mock internal lateinit var config: Config
  @Mock internal lateinit var raft: Raft
  @Mock internal lateinit var server: Server

  private lateinit var unitUnderTest: Boot

  @Before
  @Throws(Exception::class)
  fun setup() {
    MockitoAnnotations.initMocks(this)

    unitUnderTest = Boot(config, raft, server)
  }

  @Test
  @Throws(IOException::class)
  fun should_start_server_with_port() {
    // Given
    doReturn(PORT)
        .`when`<Config>(config)
        .getAsInteger(Config.KEY_PORT)

    // When
    unitUnderTest.start()

    // Then
    verify<Server>(server).start(PORT)
  }

  @Test
  fun should_shutdown_server() {
    // Given / When
    unitUnderTest.shutdown()

    // Then
    verify<Server>(server).shutdown()
  }

  @Test
  @Throws(InterruptedException::class)
  fun should_awaitTermination_on_awaitTermination() {
    // Given / When
    unitUnderTest.awaitTermination()

    // Then
    verify<Server>(server).awaitTermination()
  }

  companion object {
    private const val PORT = 10000
  }
}

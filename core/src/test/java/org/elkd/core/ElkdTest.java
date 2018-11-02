package org.elkd.core;

import org.elkd.core.config.Config;
import org.elkd.core.server.Server;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

public class ElkdTest {
  private static final int PORT = 10000;

  @Mock Config mConfig;
  @Mock
  Server mServer;

  private Elkd mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);

    mUnitUnderTest = new Elkd(mConfig, mServer);
  }

  @Test
  public void should_start_server_with_port() throws IOException {
    // Given
    doReturn(PORT)
        .when(mConfig)
        .getAsInteger(Config.KEY_SERVER_PORT);

    // When
    mUnitUnderTest.start();

    // Then
    verify(mServer).start(PORT);
  }

  @Test
  public void should_shutdown_server() {
    // Given / When
    mUnitUnderTest.stop();

    // Then
    verify(mServer).shutdown();
  }
}

package org.elkd.core.cluster;

import org.elkd.core.config.Config;
import org.elkd.shared.schemes.URI;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class ClusterUtilsTest {

  @Test
  public void should_build_node_with_host_and_port() {
    // Given
    final String host = URI.LOOPBACK_HOST;
    final int port = 9191;
    final Config config = mock(Config.class);
    doReturn(host)
        .when(config)
        .get(Config.KEY_HOST);
    doReturn(port)
        .when(config)
        .getAsInteger(Config.KEY_PORT);

    // When
    final Node node = ClusterUtils.buildSelfNode(config);

    // Then
    assertEquals(host, node.getHost());
    assertEquals(port, node.getPort());
  }
}

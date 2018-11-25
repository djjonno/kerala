package org.elkd.core.cluster;

import com.google.common.collect.ImmutableSet;
import org.elkd.shared.schemes.URI;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class ClusterConnectionPoolTest {
  private static final Node SELF_NODE = spy(new Node(URI.parseURIString("localhost:9190")));
  private static final Node NODE_1 = spy(new Node(URI.parseURIString("localhost:9191")));
  private static final Node NODE_2 = spy(new Node(URI.parseURIString("localhost:9192")));
  private static final Set<Node> NODES = ImmutableSet.of(
      NODE_1,
      NODE_2
  );

  private ClusterSet mClusterSet;

  @Before
  public void setup() throws Exception {
    mClusterSet = mock(ClusterSet.class);
    doReturn(NODES)
        .when(mClusterSet)
        .getNodes();
    doReturn(SELF_NODE)
        .when(mClusterSet)
        .getSelfNode();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  @Test
  public void should_create_channel_for_external_nodes_in_clusterSet() {
    // Given
    final ClusterConnectionPool clusterConnectionPool = new ClusterConnectionPool(mClusterSet);

    // When
    clusterConnectionPool.initialize();

    // Then
    assertNotNull(clusterConnectionPool.getChannel(NODE_1));
    assertNotNull(clusterConnectionPool.getChannel(NODE_2));
    verify(NODE_1).getURI();
    verify(NODE_2).getURI();
    assertNull(clusterConnectionPool.getChannel(SELF_NODE));
    verifyZeroInteractions(SELF_NODE);
  }
}

package org.elkd.core.consensus;

import org.elkd.core.config.Config;
import org.elkd.core.server.cluster.ClusterSet;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class RaftFactoryTest {

  @Test
  public void should_return_raft_with_clusterSet() {
    // Given
    final ClusterSet clusterSet = mock(ClusterSet.class);

    // When
    final Raft raft = RaftFactory.create(mock(Config.class), clusterSet);

    // Then
    assertSame(clusterSet, raft.getClusterSet());
  }
}

package org.elkd.core.consensus;

import org.elkd.core.config.Config;
import org.elkd.core.log.Log;
import org.elkd.core.log.LogCommandExecutor;
import org.elkd.core.log.LogProvider;
import org.elkd.core.server.cluster.ClusterSet;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class RaftFactoryTest {

  @Mock ClusterSet mClusterSet;
  @Mock Log mLog;
  @Mock LogProvider mLogProvider;
  @Mock LogCommandExecutor mLogCommandExecutor;

  @Before
  public void setup() throws Exception {
    MockitoAnnotations.initMocks(this);
    doReturn(mLog)
        .when(mLogProvider)
        .getLog();
    doReturn(mLogCommandExecutor)
        .when(mLogProvider)
        .logCommandExecutor();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void should_return_raft_with_properties() {
    // Given / When
    final Raft raft = RaftFactory.create(mock(Config.class), mLogProvider, mClusterSet);

    // Then
    assertSame(mClusterSet, raft.getClusterSet());
    assertSame(mLog, raft.getLog());
    assertSame(mLogCommandExecutor, raft.getLogCommandExecutor());
  }
}

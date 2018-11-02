package org.elkd.core.server;

import com.google.common.base.Preconditions;
import org.apache.log4j.Logger;
import org.elkd.core.consensus.RaftDelegate;

public class Server {
  private static final Logger LOG = Logger.getLogger(Server.class);

  private final RaftDelegate mRaftDelegate;

  public Server(final RaftDelegate raftDelegate) {
    mRaftDelegate = Preconditions.checkNotNull(raftDelegate, "raftDelegate");
  }

  public void start(final int port) {
    LOG.info("starting server on :" + port);
  }

  public void shutdown() {
    LOG.info("shutting down server");
  }
}

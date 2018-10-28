package org.elkd.core.cluster;

public class Node {
  private String mHostUri;

  public Node(final String hostUri) {
    mHostUri = hostUri;
  }

  public String getHostUri() {
    return mHostUri;
  }
}

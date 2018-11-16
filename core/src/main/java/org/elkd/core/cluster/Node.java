package org.elkd.core.cluster;

import com.google.common.base.Preconditions;

import java.net.URI;
import java.util.Objects;

public class Node {
  private String mId;
  private String mHostUri;
  private URI mUri;

  public Node(final String id, final String hostUri) {
    mId = Preconditions.checkNotNull(id, "id");
    mHostUri = Preconditions.checkNotNull(hostUri, "hostUri");
    mUri = URI.create(hostUri);
  }

  public String getId() {
    return mId;
  }

  public String getHostUri() {
    return mHostUri;
  }

  public String getHost() {
    return mUri.getHost();
  }

  public int getPort() {
    return mUri.getPort();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final Node node = (Node) o;
    return Objects.equals(mId, node.mId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mId);
  }

  @Override
  public String toString() {
    return "Node{" +
        "mId='" + mId + '\'' +
        ", mHostUri=" + mHostUri +
        '}';
  }
}

package org.elkd.core.cluster;

import com.google.common.base.Preconditions;

import java.net.URI;
import java.util.Objects;

public class Node {
  private String mHostUri;
  private URI mUri;

  public Node(final String hostUri) {
    mHostUri = Preconditions.checkNotNull(hostUri, "hostUri");
    mUri = URI.create(hostUri);
  }

  public String getId() {
    return String.valueOf(Objects.hashCode(mHostUri));
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
    return Objects.equals(mHostUri, node.mHostUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mHostUri);
  }

  @Override
  public String toString() {
    return "Node{" + mHostUri + '}';
  }
}

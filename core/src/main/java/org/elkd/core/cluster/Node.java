package org.elkd.core.cluster;

import com.google.common.base.Preconditions;
import org.elkd.shared.schemes.URI;

import java.util.Objects;

public class Node {
  private URI mUri;

  public Node(final URI uri) {
    mUri = Preconditions.checkNotNull(uri, "uri");
  }

  public String getId() {
    return String.valueOf(mUri.hashCode());
  }

  public URI getURI() {
    return mUri;
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
    return Objects.equals(mUri, node.mUri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(mUri);
  }

  @Override
  public String toString() {
    return mUri.toString();
  }
}

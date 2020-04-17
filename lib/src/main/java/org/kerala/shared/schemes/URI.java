package org.kerala.shared.schemes;

import com.google.common.base.Preconditions;

import java.net.InetAddress;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Utility class for manipulating, parsing, referencing URIs.
 *
 * e.g URI = kerala://<host>:<port>
 */
public class URI {

  /**
   * Default Kerala URI scheme. Serves no real purpose apart from some
   * visibility to users for Kerala related URIs.
   */
  public static final String SCHEME = "kerala://";

  /**
   * Loopback default.
   */
  public static final String LOOPBACK_HOST = InetAddress
      .getLoopbackAddress()
      .getHostName();

  private static final int HOST_INDEX = 0;
  private static final int PORT_INDEX = 1;

  private final String mHost;
  private final int mPort;

  URI(final String host, final int port) {
    Preconditions.checkState(host != null && !host.isEmpty() && isValidIPAddress(host), "host");
    Preconditions.checkState(port > 0, "port");

    mHost = normalizeHost(host);
    mPort = port;
  }

  public String getHost() {
    return mHost;
  }

  public int getPort() {
    return mPort;
  }

  @Override
  public String toString() {
    return mHost + ":" + mPort;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    final URI uri = (URI) o;
    return toString().equals(uri.toString());
  }

  @Override
  public int hashCode() {
    return Objects.hash(mHost, mPort);
  }

  public static URI parseURIString(final String uri) {
    Preconditions.checkNotNull(uri, "uri");

    final String stripped = stripScheme(uri);
    final String[] split = stripped.split(":");

    Preconditions.checkState(split.length == 2, "Malformed URI " + uri);

    final String host = split[HOST_INDEX];
    final int port;
    try {
      port = Integer.parseUnsignedInt(split[PORT_INDEX]);
    } catch (final NumberFormatException e) {
      throw new InvalidPortException(split[PORT_INDEX]);
    }

    return new URI(host, port);
  }

  private static String stripScheme(final String uri) {
    if (uri.contains(SCHEME)) {
      return uri.substring(SCHEME.length());
    }
    return uri;
  }

  private static String normalizeHost(final String host) {
    switch (host) {
      case "127.0.0.1": case "0.0.0.0": case "localhost": case "":
        return LOOPBACK_HOST;
      default:
        return host;
    }
  }

  private static boolean isValidIPAddress(final String ip) {
    String IPV4_PATTERN = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    Pattern pattern = Pattern.compile(IPV4_PATTERN);

    return pattern.matcher(ip).matches();
  }
}

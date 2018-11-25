package org.elkd.shared.schemes;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class URITest {
  private static final String HOST = URI.LOOPBACK_HOST;
  private static final int PORT = 9191;

  @Test
  public void should_parse_string_with_host_and_port() {
    // Given
    final String uriString = URI.SCHEME + HOST + ":" + PORT;

    // When
    final URI uri = URI.parseURIString(uriString);

    // Then
    assertEquals(HOST, uri.getHost());
    assertEquals(PORT, uri.getPort());
  }

  @Test
  public void should_accept_domainname_for_host() {
    // TODO: add unit test for domain name
  }

  @Test
  public void should_default_host_to_DEFAULT() {
    // Given
    final String localhost = URI.SCHEME + "localhost:9191";

    // When
    URI uri = URI.parseURIString(localhost);

    // Then
    assertEquals(URI.LOOPBACK_HOST, uri.getHost());

    // Given
    final String zerohost = URI.SCHEME + "0.0.0.0:9191";

    // When
    uri = URI.parseURIString(zerohost);

    // Then
    assertEquals(URI.LOOPBACK_HOST, uri.getHost());
  }

  @Test
  public void should_allow_scheme_to_be_absent() {
    // Given
    final String uriString = HOST + ":" + PORT;

    // When
    final URI uri = URI.parseURIString(uriString);

    // Then
    assertEquals(HOST, uri.getHost());
    assertEquals(PORT, uri.getPort());
  }

  @Test(expected = IllegalStateException.class)
  public void should_throw_exception_with_malformed_uri_string() {
    // Given / When
    URI.parseURIString(URI.SCHEME + ":localhost:9191");

    // Then - exception thrown
  }

  @Test(expected = InvalidPortException.class)
  public void should_throw_exception_with_alpha_port() {
    // Given
    URI.parseURIString(URI.SCHEME + "localhost:abc");

    // Then - exception thrown
  }

  @Test(expected = InvalidPortException.class)
  public void should_throw_exception_with_negative_val_port() {
    // Given
    URI.parseURIString(URI.SCHEME + "localhost:-1");

    // Then - exception thrown
  }
}

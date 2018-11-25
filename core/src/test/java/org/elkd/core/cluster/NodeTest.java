package org.elkd.core.cluster;

import org.elkd.shared.schemes.URI;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NodeTest {
  private static final String HOST = URI.LOOPBACK_HOST;
  private static final int PORT = 9191;
  private static final URI HOST_URI = URI.parseURIString(URI.SCHEME + HOST + ":" + PORT);

  private Node mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    mUnitUnderTest = new Node(HOST_URI);
  }

  @Test
  public void should_have_properties() {
    // Given / When - constructor

    // Then
    assertNotNull(mUnitUnderTest.getId());
    assertEquals(HOST, mUnitUnderTest.getHost());
    assertEquals(PORT, mUnitUnderTest.getPort());
    assertEquals(HOST_URI, mUnitUnderTest.getURI());
  }

  @Test
  public void should_be_equal() {
    // Given / When
    final Node first = new Node(URI.parseURIString(URI.SCHEME + HOST + ":" + PORT));
    final Node second = new Node(URI.parseURIString(URI.SCHEME + HOST + ":" + PORT));

    // Then
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }
}

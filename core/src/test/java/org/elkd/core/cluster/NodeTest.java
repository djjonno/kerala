package org.elkd.core.cluster;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NodeTest {
  private static final String ID = "id";
  private static final String HOST = "localhost";
  private static final int PORT = 9191;
  private static final String HOST_URI = "elkd://" + HOST + ":" + PORT;

  private Node mUnitUnderTest;

  @Before
  public void setup() throws Exception {
    mUnitUnderTest = new Node(ID, HOST_URI);
  }

  @Test
  public void should_have_properties() {
    // Given / When - constructor

    // Then
    assertEquals(ID, mUnitUnderTest.getId());
    assertEquals(HOST, mUnitUnderTest.getHost());
    assertEquals(PORT, mUnitUnderTest.getPort());
    assertEquals(HOST_URI, mUnitUnderTest.getHostUri());
  }

  @Test
  public void should_be_equal() {
    // Given / When
    final String id = "sameId";
    final Node first = new Node(id, HOST_URI);
    final Node second = new Node(id, "differentHost");

    // Then
    assertEquals(first, second);
    assertEquals(first.hashCode(), second.hashCode());
  }
}
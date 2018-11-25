package org.elkd.core.cluster;

import com.google.common.collect.ImmutableSet;
import org.elkd.core.ElkdRuntimeException;
import org.elkd.shared.schemes.URI;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class StaticClusterSetTest {
  private static final Node SELF_NODE = new Node(URI.parseURIString("127.0.0.1:9191"));
  private static final Node NODE_1 = new Node(URI.parseURIString("127.0.0.1:9192"));
  private static final Node NODE_2 = new Node(URI.parseURIString("127.0.0.1:9193"));
  private static final Set<Node> MEMBERS = ImmutableSet.of(
      NODE_1, NODE_2
  );

  @Test
  public void should_create_cluster_config() {
    // Given / When
    final StaticClusterSet set = StaticClusterSet.builder(SELF_NODE)
        .withNode(NODE_1)
        .withNode(NODE_2)
        .build();

    // Then
    assertEquals(MEMBERS.size(), set.clusterSize());
    assertEquals(MEMBERS, set.getNodes());
  }

  @Test
  public void should_not_add_member_to_static_config() {
    // Given
    final StaticClusterSet set = StaticClusterSet.builder(SELF_NODE)
        .withNode(NODE_1)
        .withNode(NODE_2)
        .build();
    final Node member = new Node(URI.parseURIString("127.0.0.1:9191"));

    // When
    set.addNode(member);


    // Then
    assertEquals(MEMBERS, set.getNodes());
  }

  @Test
  public void should_not_remove_member_from_static_config() {
    // Given
    final StaticClusterSet set = StaticClusterSet.builder(SELF_NODE)
        .withNode(NODE_1)
        .withNode(NODE_2)
        .build();

    // When
    set.removeNode((Node) MEMBERS.toArray()[0]);

    // Then
    assertEquals(MEMBERS, set.getNodes());
  }

  @Test
  public void should_parse_clusterSet_string_into_nodes() {
    // Given
    final String clusterSet = NODE_1.getURI() + "," + NODE_2.getURI();

    // When
    final StaticClusterSet set = StaticClusterSet.builder(SELF_NODE)
        .withString(clusterSet)
        .build();

    // Then
    assertEquals(MEMBERS, set.getNodes());
  }

  @Test
  public void should_tolerate_trailing_comma_in_clusterSet_string() {
    // Given
    final String clusterSet = NODE_1.getURI() + "," + NODE_2.getURI() + ",";

    // When
    final StaticClusterSet set = StaticClusterSet.builder(SELF_NODE)
        .withString(clusterSet)
        .build();

    // Then
    assertEquals(MEMBERS, set.getNodes());
  }

  @Test
  public void should_have_correct_size() {
    // Given / When
    final StaticClusterSet set = StaticClusterSet.builder(SELF_NODE)
        .withNode(NODE_1)
        .withNode(NODE_2)
        .build();

    // Then
    assertEquals(set.clusterSize(), MEMBERS.size());
  }

  @Test(expected = ElkdRuntimeException.class)
  public void should_contain_unique_nodes() {
    // Given
    final Node duplicate = new Node(NODE_1.getURI());

    // When
    StaticClusterSet.builder(SELF_NODE)
        .withNode(NODE_1)
        .withNode(NODE_2)
        .withNode(duplicate)
        .build();

    // Then - throws exception
  }

  @Test
  public void should_throw_exception_when_adding_self_node() {
    // Given / When
    final StaticClusterSet clusterSet = StaticClusterSet.builder(SELF_NODE)
        .withNode(SELF_NODE)
        .build();

    // Then
    assertFalse(clusterSet.getNodes().contains(SELF_NODE));
  }

  @Test
  public void should_not_add_to_cluster_set_if_self_node_as_string() {
    // Given / When
    final StaticClusterSet clusterSet = StaticClusterSet.builder(SELF_NODE)
        .withString(SELF_NODE.getHost() + ":" + SELF_NODE.getPort())
        .build();

    // Then
    assertFalse(clusterSet.getNodes().contains(SELF_NODE));
  }
}

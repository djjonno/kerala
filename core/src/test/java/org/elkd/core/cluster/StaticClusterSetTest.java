package org.elkd.core.cluster;

import com.google.common.collect.ImmutableSet;
import org.elkd.core.ElkdRuntimeException;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class StaticClusterSetTest {
  private static final Node NODE_1 = new Node("elkd://node-1");
  private static final Node NODE_2 = new Node("elkd://node-2");
  private static final Set<Node> MEMBERS = ImmutableSet.of(
      NODE_1, NODE_2
  );

  @Test
  public void should_create_cluster_config() {
    // Given / When
    final StaticClusterSet set = StaticClusterSet.builder()
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
    final StaticClusterSet set = StaticClusterSet.builder()
        .withNode(NODE_1)
        .withNode(NODE_2)
        .build();
    final Node member = new Node("elkd://3");

    // When
    set.addNode(member);


    // Then
    assertEquals(MEMBERS, set.getNodes());
  }

  @Test
  public void should_not_remove_member_from_static_config() {
    // Given
    final StaticClusterSet set = StaticClusterSet.builder()
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
    final String clusterSet = NODE_1.getHostUri() + "," + NODE_2.getHostUri();

    // When
    final StaticClusterSet set = StaticClusterSet.builder().withString(clusterSet).build();

    // Then
    assertEquals(MEMBERS, set.getNodes());
  }

  @Test
  public void should_tolerate_trailing_comma_in_clusterSet_string() {
    // Given
    final String clusterSet = NODE_1.getHostUri() + "," + NODE_2.getHostUri() + ",";

    // When
    final StaticClusterSet set = StaticClusterSet.builder().withString(clusterSet).build();

    // Then
    assertEquals(MEMBERS, set.getNodes());
  }

  @Test
  public void should_have_correct_size() {
    // Given / When
    final StaticClusterSet set = StaticClusterSet.builder()
        .withNode(NODE_1)
        .withNode(NODE_2)
        .build();

    // Then
    assertEquals(set.clusterSize(), MEMBERS.size());
  }

  @Test(expected = ElkdRuntimeException.class)
  public void should_contain_unique_nodes() {
    // Given
    final Node duplicate = new Node(NODE_1.getHostUri());

    // When
    StaticClusterSet.builder()
        .withNode(NODE_1)
        .withNode(NODE_2)
        .withNode(duplicate)
        .build();

    // Then - throws exception
  }
}

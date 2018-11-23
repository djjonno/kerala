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
    final StaticClusterSet config = StaticClusterSet.builder()
        .withNode(NODE_1)
        .withNode(NODE_2)
        .build();

    // Then
    assertEquals(MEMBERS.size(), config.clusterSize());
    assertEquals(MEMBERS, config.getNodes());
  }

  @Test
  public void should_not_add_member_to_static_config() {
    // Given
    final StaticClusterSet config = StaticClusterSet.builder()
        .withNode(NODE_1)
        .withNode(NODE_2)
        .build();
    final Node member = new Node("elkd://3");

    // When
    config.addNode(member);


    // Then
    assertEquals(MEMBERS, config.getNodes());
  }

  @Test
  public void should_not_remove_member_from_static_config() {
    // Given
    final StaticClusterSet config = StaticClusterSet.builder()
        .withNode(NODE_1)
        .withNode(NODE_2)
        .build();

    // When
    config.removeNode((Node) MEMBERS.toArray()[0]);

    // Then
    assertEquals(MEMBERS, config.getNodes());
  }

  @Test
  public void should_have_correct_size() {
    // Given / When
    final StaticClusterSet config = StaticClusterSet.builder()
        .withNode(NODE_1)
        .withNode(NODE_2)
        .build();

    // Then
    assertEquals(config.clusterSize(), MEMBERS.size());
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

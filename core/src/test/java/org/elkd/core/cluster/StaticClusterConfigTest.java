package org.elkd.core.cluster;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class StaticClusterConfigTest {
  private static final Set<Node> MEMBERS = ImmutableSet.of(
      new Node("elkd://1"),
      new Node("elkd://2")
  );

  @Test
  public void should_create_cluster_config() {
    // Given / When
    final StaticClusterConfig config = new StaticClusterConfig(MEMBERS);

    // Then
    assertEquals(MEMBERS.size(), config.clusterSize());
    assertEquals(MEMBERS, config.getNodes());
  }

  @Test
  public void should_not_add_member_to_static_config() {
    // Given
    final StaticClusterConfig config = new StaticClusterConfig(MEMBERS);
    final Node member = new Node("elkd://3");

    // When
    config.addNode(member);


    // Then
    assertEquals(MEMBERS, config.getNodes());
  }

  @Test
  public void should_not_remove_member_from_static_config() {
    // Given
    final StaticClusterConfig config = new StaticClusterConfig(MEMBERS);

    // When
    config.removeNode((Node) MEMBERS.toArray()[0]);

    // Then
    assertEquals(MEMBERS, config.getNodes());
  }
}

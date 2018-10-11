package org.elkd.core.cluster;

import com.google.common.collect.ImmutableSet;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class StaticClusterConfigTest {
  private static final Set<String> MEMBERS = ImmutableSet.of(
      "elkd://1",
      "elkd://2"
  );

  @Test
  public void should_create_cluster_config() {
    // Given / When
    final StaticClusterConfig config = new StaticClusterConfig(MEMBERS);

    // Then
    assertEquals(MEMBERS.size(), config.clusterSize());
    assertEquals(MEMBERS, config.getMembers());
  }

  @Test
  public void should_not_add_member_to_static_config() {
    // Given
    final StaticClusterConfig config = new StaticClusterConfig(MEMBERS);
    final String member = "elkd://3";

    // When
    config.addClusterMember(member);


    // Then
    assertEquals(MEMBERS, config.getMembers());
  }

  @Test
  public void should_not_remove_member_from_static_config() {
    // Given
    final StaticClusterConfig config = new StaticClusterConfig(MEMBERS);

    // When
    config.removeClusterMember((String) MEMBERS.toArray()[0]);

    // Then
    assertEquals(MEMBERS, config.getMembers());
  }
}

package org.elkd.core.cluster;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

public final class StaticClusterConfig implements ClusterConfig {
  private final ImmutableSet<String> mMembers;

  public StaticClusterConfig(final Set<String> members) {
    mMembers = ImmutableSet.copyOf(members);
  }

  @Override
  public void addClusterMember(final String member) {
    // no-op, membership is static
  }

  @Override
  public void removeClusterMember(final String uri) {
    // no-op, membership is static
  }

  @Override
  public Set<String> getMembers() {
    return mMembers;
  }

  @Override
  public int clusterSize() {
    return mMembers.size();
  }
}

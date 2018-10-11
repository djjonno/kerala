package org.elkd.core.cluster;

import java.util.Set;

public interface ClusterConfig {

  int clusterSize();

  void addClusterMember(String uri);

  void removeClusterMember(String uri);

  Set<String> getMembers();

}

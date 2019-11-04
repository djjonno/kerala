package org.ravine.core.config

class Config internal constructor(val config: Map<String, String>) {

  inline operator fun <reified T> get(key: String): T {
    return when (T::class) {
      Int::class -> config[key]?.toInt() as T
      Long::class -> config[key]?.toLong() as T
      else -> config[key].toString() as T
    }
  }

  override fun toString(): String {
    return "Config{$config}"
  }

  companion object {

    /* Add Config keys here. Remember to update @{link ConfigCliSource} with config. */

    /**
     * Cluster Set.
     */
    @Key val KEY_CLUSTER = "cluster"

    /**
     * Data storage location.
     */
    @Key(defaultValue = "/usr/local/ravine") val KEY_DATA_DIR = "data.dir"

    /**
     * Server host.
     */
    @Key(defaultValue = "localhost") val KEY_HOST = "host"

    /**
     * Server port.
     */
    @Key(defaultValue = "9191") val KEY_PORT = "port"

    /**
     * Time for a follower to wait before transitioning to candidate state.
     */
    @Key(defaultValue = "2000") val KEY_RAFT_FOLLOWER_TIMEOUT_MS = "consensus.follower.timeout"

    /**
     * Time for a candidate to wait before transitioning to follower state.
     */
    @Key(defaultValue = "2000") val KEY_RAFT_ELECTION_TIMEOUT_MS = "consensus.election.timeout"

    /**
     * Time between message dispatch from leader to followers.
     */
    @Key(defaultValue = "250") val KEY_RAFT_LEADER_BROADCAST_INTERVAL_MS = "consensus.leader.interval"

    /*  -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -   -  */

    val keyDefaults: Map<String, String> = Config::class.java.declaredFields.filter {
          it.isAnnotationPresent(Key::class.java)
        }.map {
          it.get(null) as String to it.getAnnotation(Key::class.java).defaultValue
        }.toMap()

    val supportedKeys: Set<String> = keyDefaults.keys
  }
}

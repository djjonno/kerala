package org.elkd.core.runtime.client.controller

enum class SyslogCommandType(val id: String) {
  /**
   * This does not change the leader, this handlers exists when a leader is changed,
   * and down-stream consumers should be informed.  Of course you could also obtain
   * this state directly from the Raft module.
   *
   * @param node host:port of new leader.
   */
  CONSENSUS_CHANGE("consensus-change"),

  /**
   * Creates a new topic and configures it in the TopicRegistry.
   *
   * @param namespace unique name of topic.
   */
  CREATE_TOPIC("create-topic")

  ;

  companion object {
    /**
     * Commands are received from clients in string form, this helper method maps
     * a string to its enum counterpart.
     */
    fun fromId(string: String): SyslogCommandType {
      return values().first { it.id == string }
    }

    /**
     * Return Syslog availableCommandIds
     */
    val availableCommandIds: List<String> = values().map { it.id }
  }
}

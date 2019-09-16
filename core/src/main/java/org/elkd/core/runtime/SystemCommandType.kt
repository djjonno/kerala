package org.elkd.core.runtime

enum class SystemCommandType(val id: String) {

  /**
   * This does not change the leader, this handlers exists when a leader is changed,
   * and down-stream consumers should be informed.  Of course you could also obtain
   * this state directly from the Raft module.
   *
   * @param node host:port of new leader.
   */
  LEADER_CHANGE("leader-change"),

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
    fun fromString(string: String): SystemCommandType {
      return values().first { it.id == string }
    }
  }
}

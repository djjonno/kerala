package org.kerala.core.runtime.client.ctl

import org.kerala.core.consensus.OpCategory
import org.kerala.core.runtime.client.ctl.parsers.CommandParser
import org.kerala.core.runtime.client.ctl.parsers.CreateTopicCommandParser
import org.kerala.core.runtime.client.ctl.parsers.DefaultCommandParser
import org.kerala.core.runtime.client.ctl.parsers.DeleteTopicCommandParser

enum class CtlCommandType(
    val id: String,
    val parser: CommandParser,
    val category: OpCategory
) {
  /**
   * This does not change the leader, this handlers exists when a leader is changed,
   * and down-stream consumers should be informed.  Of course you could also obtain
   * this state directly from the Raft module.
   */
  CONSENSUS_CHANGE("consensus-change", DefaultCommandParser(), OpCategory.WRITE),

  /**
   * Creates a new topic and configures it in the TopicRegistry.
   */
  CREATE_TOPIC("create-topic", CreateTopicCommandParser(), OpCategory.WRITE),

  /**
   * Delete a topic.
   */
  DELETE_TOPIC("delete-topic", DeleteTopicCommandParser(), OpCategory.WRITE),

  /**
   * Describe topics detailing meta data.
   */
  DESCRIBE_TOPICS("describe-topics", DefaultCommandParser(), OpCategory.READ),

  /**
   * Describe the cluster state.
   */
  DESCRIBE_CLUSTER("describe-cluster", DefaultCommandParser(), OpCategory.READ)

  ;

  companion object {
    /**
     * Commands are received from clients in string form, this helper method maps
     * a string to its enum counterpart.
     */
    fun fromId(string: String): CtlCommandType {
      return values().first { it.id == string }
    }

    /**
     * Get writeCommands
     */
    val writeCommands = values().filter { it.category == OpCategory.WRITE }.map { it.id }
  }
}

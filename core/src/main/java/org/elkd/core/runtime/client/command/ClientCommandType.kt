package org.elkd.core.runtime.client.command

import org.elkd.core.consensus.OpCategory
import org.elkd.core.runtime.client.command.parsers.CommandParser
import org.elkd.core.runtime.client.command.parsers.CreateTopicCommandParser
import org.elkd.core.runtime.client.command.parsers.DefaultCommandParser
import org.elkd.core.runtime.client.command.parsers.DeleteTopicCommandParser

enum class ClientCommandType(
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
   * Read available Topics.
   */
  READ_TOPICS("read-topics", DefaultCommandParser(), OpCategory.READ)

  ;

  companion object {
    /**
     * Commands are received from clients in string form, this helper method maps
     * a string to its enum counterpart.
     */
    fun fromId(string: String): ClientCommandType {
      return values().first { it.id == string }
    }

    /**
     * Get writeCommands
     */
    val writeCommands = values().filter { it.category == OpCategory.WRITE }.map { it.id }
    val readCommands = values().filter { it.category == OpCategory.READ }.map { it.id }
  }
}

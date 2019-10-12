package org.elkd.core.runtime.client.command

import org.elkd.core.runtime.client.command.parsers.CommandParser
import org.elkd.core.runtime.client.command.parsers.CreateTopicCommandParser
import org.elkd.core.runtime.client.command.parsers.DefaultCommandParser
import org.elkd.core.runtime.client.command.parsers.DeleteTopicCommandParser

enum class SyslogCommandType(val id: String, val parser: CommandParser) {
  /**
   * This does not change the leader, this handlers exists when a leader is changed,
   * and down-stream consumers should be informed.  Of course you could also obtain
   * this state directly from the Raft module.
   *
   * @param node host:port of new leader.
   */
  CONSENSUS_CHANGE("consensus-change", DefaultCommandParser()),

  /**
   * Creates a new topic and configures it in the TopicRegistry.
   *
   * @param namespace unique name of Topic.
   */
  CREATE_TOPIC("create-topic", CreateTopicCommandParser()),

  /**
   * Delete a topic.
   *
   * @param namespace unique namespace of Topic.
   */
  DELETE_TOPIC("delete-topic", DeleteTopicCommandParser())

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

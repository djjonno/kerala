package org.elkd.core.runtime.client.consumer

import org.apache.log4j.Logger
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.runtime.TopicModule
import org.elkd.core.runtime.client.command.Command
import org.elkd.core.runtime.client.command.CommandType
import org.elkd.core.runtime.client.command.asCommand

/**
 * SystemConsumer consumes all entries on the system Topic.
 *
 * The entry comes in the form of a decomposed Command object,
 * serialized as KVS.  This component will extract it and
 * execute it appropriate against the runtime.
 *
 * The invocation is purely asynchronous.  The entries have
 * already been committed to the logFacade so the failure mode here
 * it simply to logFacade.error and continue.
 *
 * It is a best-effort strategy to ensure an invalid type
 * is not issued to the logFacade in the first place, so it is rare
 * that you will encounter a runtime failure.  It will likely
 * be some kind of systemic error as opposed to an invalid
 * state of some sorts.
 */
class SystemConsumer(private val topicModule: TopicModule) : Consumer {
  override fun consume(index: Long, entry: Entry) {
    val command = entry.asCommand()

    when (CommandType.fromString(command.command)) {
      CommandType.CREATE_TOPIC -> createTopic(command.CreateTopicCommand())
      CommandType.CONSENSUS_CHANGE -> LOGGER.info("leader changed.")
    }
  }

  private fun createTopic(command: Command.CreateTopicCommand) {
    val newTopic = topicModule.provisionNewTopic(command.id, command.namespace)
    LOGGER.info("Provisioned new topic $newTopic")
  }

  companion object {
    private var LOGGER = Logger.getLogger(SystemConsumer::class.java)
  }
}

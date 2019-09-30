package org.elkd.core.runtime.client.consumer

import org.apache.log4j.Logger
import org.elkd.core.consensus.messages.Entry
import org.elkd.core.runtime.TopicModule
import org.elkd.core.runtime.client.command.Command

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
 * It is a best-effort strategy to ensure an invalid command
 * is not issued to the logFacade in the first place, so it is rare
 * that you will encounter a runtime failure.  It will likely
 * be some kind of systemic error as opposed to an invalid
 * state of some sorts.
 */
class SystemConsumer(val topicModule: TopicModule) : Consumer {
  override fun consume(index: Long, entry: Entry) {
    logger.info("consuming system entry -> $entry")

    val map = entry.kvs.map { it.key to it.`val` }.toMap()

    map["cmd"]?.apply {
      when (Command.Type.fromString(this)) {
        Command.Type.CREATE_TOPIC -> createTopic(map)
        Command.Type.LEADER_CHANGE -> { logger.info("leader changed.") }
      }
    }
  }

  private fun createTopic(kvs: Map<String, String>) {
//    topicModule.topicRegistry.add(Topic(kvs["namespace"]!!))
    logger.info(topicModule.topicRegistry)
  }

  companion object {
    private var logger = Logger.getLogger(SystemConsumer::class.java)
  }
}

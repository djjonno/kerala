package org.kerala.core.consensus

import org.kerala.core.consensus.messages.Entry
import org.kerala.core.consensus.messages.KV
import org.kerala.core.log.LogChangeEvent
import org.kerala.core.log.LogChangeReason
import org.kerala.core.log.LogChangeRegistry
import org.kerala.core.log.commands.AppendCommand
import org.kerala.core.runtime.topic.Topic
import org.kerala.shared.logger
import java.time.Duration
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

data class ReadResult(val offset: Long, val entries: List<Entry>)

class ConsensusFacade(private val raft: Raft) {

  val delegator: RaftDelegator
    get() = raft.delegator

  private val productionHandlers = mutableSetOf<Pair<Topic, LogChangeRegistry<Entry>.CompletionHandler>>()

  fun initialize() = raft.initialize()

  fun supportsCategory(category: OpCategory): Boolean = category in raft.supportedOps

  fun writeToTopic(
      topic: Topic,
      kvs: List<KV>,
      onCommit: () -> Unit,
      onFailure: (Exception) -> Unit = {},
      timeout: Duration? = null
  ): LogChangeRegistry<Entry>.CompletionHandler {
    val entry = Entry.builder(raft.raftContext.currentTerm).addAllKV(kvs).build()
    return topic.logFacade.changeRegistry.register(entry, LogChangeEvent.COMMIT, onCommit, onFailure, timeout).apply {
      val command = AppendCommand.build(entry, LogChangeReason.CLIENT)
      topic.logFacade.commandExecutor.execute(command)
      productionHandlers.add(Pair(topic, this))
    }
  }

  suspend fun writeToTopic(topic: Topic, kvs: List<KV>, timeout: Duration? = null) {
    suspendCoroutine<Unit> { cont ->
      writeToTopic(topic, kvs, { cont.resume(Unit) }, { cont.resumeWithException(it) }, timeout)
    }
  }

  suspend fun readFromTopic(topic: Topic, index: Long?): ReadResult {
    return suspendCoroutine { cont ->
      /**
       * readBlock used to synchronize all external log activity,
       * eliminating race conditions.
       */
      topic.logFacade.readBlock {
        with(topic.logFacade.log) {
          val i: Long = index ?: commitIndex + 1 // read ahead entry

          if (i <= commitIndex) {
            read(i)?.let {
              cont.resume(ReadResult(offset = i, entries = listOf(it)))
            } ?: cont.resumeWithException(Exception("wtf - log misread"))
          } else {
            /* If requested index is not yet committed, perform a readAhead */
            readAhead(topic, i, cont)
          }
        }
      }
    }
  }

  /**
   * Allow client to make a readAhead request.
   */
  private fun readAhead(topic: Topic, index: Long, cont: Continuation<ReadResult>) {
    logger("reading ahead since $index does not yet exist on $topic")
    topic.logFacade.changeRegistry.register(index, LogChangeEvent.COMMIT, {
      topic.logFacade.log.read(index)?.let {
        cont.resume(ReadResult(offset = index, entries = listOf(it)))
      } ?: cont.resumeWithException(Exception("wtf - log misread"))
    },
        {
          cont.resumeWithException(Exception("wtf - log misread"))
        }
    )
  }
}

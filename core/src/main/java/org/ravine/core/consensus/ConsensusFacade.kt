package org.ravine.core.consensus

import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import org.ravine.core.consensus.messages.Entry
import org.ravine.core.consensus.messages.KV
import org.ravine.core.log.LogChangeEvent
import org.ravine.core.log.LogChangeReason
import org.ravine.core.log.LogChangeRegistry
import org.ravine.core.log.LogChangeRegistry.CancellationReason
import org.ravine.core.log.commands.AppendCommand
import org.ravine.core.runtime.topic.Topic

class ConsensusFacade(private val raft: Raft) {

  val delegator: RaftDelegator
    get() = raft.delegator

  val raftContext = raft.raftContext

  fun initialize() = raft.initialize()

  /*
   * Check if consensus state supports the given operation.
   */
  fun supportsCategory(category: OpCategory): Boolean = category in raft.supportedOps

  fun writeToTopic(
      topic: Topic,
      kvs: List<KV>,
      onCommit: () -> Unit,
      onFailure: (CancellationReason) -> Unit = {}
  ): LogChangeRegistry<Entry>.CompletionHandler {
    val entry = Entry.builder(raft.raftContext.currentTerm).addAllKV(kvs).build()
    val handler = topic.logFacade.changeRegistry.register(entry, LogChangeEvent.COMMIT, onCommit, onFailure)
    val command = AppendCommand.build(entry, LogChangeReason.CLIENT)
    topic.logFacade.commandExecutor.execute(command)
    return handler
  }

  suspend fun writeToTopic(topic: Topic, kvs: List<KV>) {
    suspendCoroutine<Unit> { cont ->
      writeToTopic(topic, kvs, { cont.resume(Unit) }, { cont.resumeWithException(Exception()) })
    }
  }

  suspend fun readFromTopic(topic: Topic, index: Long?): List<Entry> {
    return suspendCoroutine { cont ->
      /**
       * readBlock used to synchronize all external log activity,
       * eliminating race conditions.
       */
      topic.logFacade.readBlock {
        with(topic.logFacade.log) {
          val i: Long = index ?: commitIndex + 1 // next entry

          if (i <= commitIndex) {
            read(i)?.let {
              cont.resume(listOf(it))
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
  private fun readAhead(topic: Topic, i: Long, cont: Continuation<List<Entry>>) {
    topic.logFacade.changeRegistry.register(i, LogChangeEvent.COMMIT,
        {
          topic.logFacade.log.read(i)?.let {
            cont.resume(listOf(it))
          } ?: cont.resumeWithException(Exception("wtf - log misread"))
        },
        {
          cont.resumeWithException(Exception("wtf - log misread"))
        }
    )
  }
}

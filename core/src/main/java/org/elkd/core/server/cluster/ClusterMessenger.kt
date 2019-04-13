package org.elkd.core.server.cluster

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import org.apache.log4j.Logger
import org.elkd.core.ElkdRuntimeException
import org.elkd.core.consensus.messages.AppendEntriesRequest
import org.elkd.core.consensus.messages.RequestVoteRequest
import org.elkd.core.server.cluster.exceptions.NodeNotFoundException
import org.elkd.core.server.converters.ConverterRegistry
import org.elkd.shared.annotations.Mockable
import kotlin.coroutines.CoroutineContext

@Mockable
class ClusterMessengerV2
@JvmOverloads constructor(private val clusterConnectionPool: ClusterConnectionPool,
                          private val converterRegistry: ConverterRegistry = ConverterRegistry.getInstance()): CoroutineScope {
  val job: Job
    get() = Job()
  val clusterSet: ClusterSet
    get() = clusterConnectionPool.clusterSet
  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.IO

  /**
   * Send message to a node in the cluster.
   *
   * @param node Node to message
   * @param message Message to send.  Supported types AppendEntriesRequest, RequestVoteRequest
   * @param onSuccess Block to call with message response on success
   * @param onFailure (@optional) Call this with exception on a message delivery failure
   *
   * @see AppendEntriesRequest
   * @see RequestVoteRequest
   */
  suspend fun <T> dispatch(node: Node,
                           message: Any,
                           onSuccess: (result: T) -> Unit = {},
                           onFailure: (e: ElkdRuntimeException) -> Unit = {}): T? {
    LOG.info("dispatching message to $node")
    val channel = getChannel(node)
    val response = coroutineScope {
      try {
        when (message) {
          is AppendEntriesRequest -> channel.appendEntries(converterRegistry.convert(message)).get()
          is RequestVoteRequest -> channel.requestVote(converterRegistry.convert(message)).get()
          else -> onFailure(ElkdRuntimeException("Unsupported message type ${message.javaClass}"))
        }
      } catch (e: Exception) {
        onFailure(ElkdRuntimeException(e))
        return@coroutineScope e
      }
    }

    /* TODO: improve this code block */
    return if (response !is Unit) {
      with (converterRegistry.convert<T>(response)) {
        onSuccess(this)
        this
      }
    } else null
  }

  private fun getChannel(node: Node): ClusterConnectionPool.Channel {
    return (clusterConnectionPool.getChannel(node) ?: throw NodeNotFoundException())
  }

  companion object {
    private val LOG = Logger.getLogger(ClusterMessengerV2::class.java)
  }
}

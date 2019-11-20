package org.kerala.ctl.commands.consumer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.long
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.kerala.core.server.client.ClientServiceGrpc
import org.kerala.ctl.Context
import org.kerala.shared.client.ConsumerACK

class ConsoleConsumerCommand : CliktCommand(name = "console-consumer"),
    CoroutineScope by MainScope() {

  val index: Long by option("-i", "--index").long().default(0)
  val topic: String by argument(name = "namespace", help = "namespace of topic to consume from")

  override fun run() = runBlocking {
    echo("consuming <- Topic($topic) @ $index")
    echo("-")

    val streamConsumer = StreamConsumer(ClientServiceGrpc.newStub(Context.channel))
    var increment = index
    loop@do {
      streamConsumer.batch(topic, increment)
      val response = streamConsumer.channel.receive()
      when (response.status) {
        ConsumerACK.Codes.OK.id -> {
          response.kvsList.forEach {
            echo("${it.key}/${it.value}")
          }
          increment++
        }
        else -> {
          echo("error status=${response.status}")
          break@loop
        }
      }
    } while (true)
  }
}

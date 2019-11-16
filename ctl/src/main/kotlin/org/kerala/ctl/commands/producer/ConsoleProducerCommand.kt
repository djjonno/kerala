package org.kerala.ctl.commands.producer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi
import com.github.ajalt.clikt.parameters.arguments.argument
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.runBlocking
import org.kerala.core.server.client.ClientServiceGrpc
import org.kerala.core.server.client.RpcKV
import org.kerala.ctl.Context
import org.kerala.shared.client.ProducerACK

class ConsoleProducerCommand : CliktCommand(name = "console-producer"),
    CoroutineScope by MainScope() {

  val topic: String by argument(name = "namespace", help = "namespace of topic to consume from")

  override fun run() = runBlocking {
    echo("producing -> Topic($topic)")
    echo("-")

    val producer = ProducerChannel(ClientServiceGrpc.newStub(Context.channel))
    var produce = true
    do {
      val prompt = TermUi.prompt("> ")
      when (val status = producer.batch(topic, listOf(RpcKV.newBuilder().setValue(prompt).build()))) {
        ProducerACK.Codes.OK.id -> echo("committed ✓")
        else -> {
          produce = false
          echo("error status=$status")
        }
      }
    } while (produce)
  }
}

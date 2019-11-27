package org.kerala.ctl

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.findObject
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import io.grpc.ManagedChannelBuilder
import org.kerala.ctl.commands.BaseReadCommand
import org.kerala.ctl.commands.CreateTopicCommand
import org.kerala.ctl.commands.DeleteTopicCommand
import org.kerala.ctl.commands.consumer.ConsoleConsumerCommand
import org.kerala.ctl.commands.producer.ConsoleProducerCommand
import org.kerala.shared.client.ClientACK
import org.kerala.shared.client.CtlClusterDescription
import org.kerala.shared.client.CtlReadTopics
import org.kerala.shared.json.GsonUtils
import org.kerala.shared.schemes.URI
import kotlin.system.exitProcess

class Tool : CliktCommand(name = "kerala-ctl") {
  private val quiet by option("--quiet", "-q", help = "run command w/out ceremony").flag()
  private val broker by option("-b", "--broker", metavar = "host:port", help = "broker to run command against").validate { option ->
    require(option.split(":").size == 2) { "--broker requires format `hostname:port`" }
  }
  private val ctx by findObject { Context() }

  override fun run() {
    if (!quiet) {
      echo("kerala ctl (v0.1.0)\n")
    }

    try {
      bootstrapCluster()
    } catch (e: Exception) {
      echo(e.message)
      exitProcess(1)
    }
  }

  private fun bootstrapCluster() {
    val uri = URI.parseURIString(broker)
    val channel = ManagedChannelBuilder
        .forAddress(uri.host, uri.port)
        .usePlaintext()
        .build()
    val response = sendCommand(channel, "cluster")
    try {
      when(response.status) {
        ClientACK.Codes.OK.id -> ctx.cluster = GsonUtils.buildGson().fromJson(response.response, CtlClusterDescription::class.java)
        ClientACK.Codes.ERROR.id -> throw Exception("could not bootstrap cluster (${response.response})")
      }
    } catch (e: Exception) {
      throw Exception(e)
    }
  }
}

fun main(args: Array<String>) = Tool()
    .subcommands(
        BaseReadCommand<CtlClusterDescription>("cluster") { GsonUtils.buildGson().fromJson(it, CtlClusterDescription::class.java) },
        BaseReadCommand<CtlReadTopics>("topics") { GsonUtils.buildGson().fromJson(it, CtlReadTopics::class.java) },

        CreateTopicCommand(),
        DeleteTopicCommand(),

        ConsoleConsumerCommand(),
        ConsoleProducerCommand()
    )
    .main(args)

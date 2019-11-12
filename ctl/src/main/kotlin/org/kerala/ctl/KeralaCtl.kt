package org.kerala.ctl

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import io.grpc.ManagedChannelBuilder
import org.kerala.ctl.commands.BaseReadCommand
import org.kerala.ctl.commands.CreateTopicCommand
import org.kerala.ctl.commands.DeleteTopicCommand
import org.kerala.shared.client.ClusterDescription
import org.kerala.shared.client.ReadTopics
import org.kerala.shared.json.GsonUtils

class Tool : CliktCommand(name = "kerala-ctl") {
  val quiet by option("--quiet", "-q", help = "run command w/out ceremony").flag()

  private val broker by option("-b", "--broker", metavar = "host:port", help = "broker to run command against").validate { option ->
    require(option.split(":").size == 2) { "--broker requires format `hostname:port`" }
  }

  override fun run() {
    if (!quiet) {
      echo("kerala ctl (v0.1.0)")
      echo("")
    }

    val parts = broker!!.split(":")
    Context.channel = ManagedChannelBuilder
        .forAddress(parts.first(), parts.last().toInt())
        .usePlaintext()
        .build()
  }
}

fun main(args: Array<String>) = Tool()
    .subcommands(
        BaseReadCommand<ClusterDescription>("describe-cluster") { GsonUtils.buildGson().fromJson(it, ClusterDescription::class.java) },
        BaseReadCommand<ReadTopics>("describe-topics", { GsonUtils.buildGson().fromJson(it, ReadTopics::class.java) }),

        CreateTopicCommand(),
        DeleteTopicCommand()
    )
    .main(args)

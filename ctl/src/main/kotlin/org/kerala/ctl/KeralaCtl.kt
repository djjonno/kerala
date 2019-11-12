package org.kerala.ctl

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import io.grpc.ManagedChannelBuilder
import org.kerala.ctl.commands.ClusterInfoCommand

class Tool : CliktCommand(name = "kerala-ctl") {
  private val broker by option("-b", "--broker", metavar = "host:port", help = "broker to run command against").validate { option ->
    require(option.split(":").size == 2) { "--broker requires format `hostname:port`" }
  }

  override fun run() {
    val parts = broker!!.split(":")
    Context.channel = ManagedChannelBuilder
        .forAddress(parts.first(), parts.last().toInt())
        .usePlaintext()
        .build()
  }
}

fun main(args: Array<String>) = Tool()
    .subcommands(ClusterInfoCommand())
    .main(args)

package org.kerala.ctl.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.requireObject
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.kerala.ctl.Context
import org.kerala.ctl.any
import org.kerala.ctl.asChannel
import org.kerala.ctl.sendCommand
import org.kerala.shared.client.ClientACK

typealias JsonTransformer<T> = (jsonString: String) -> T

class BaseReadCommand<T>(private val command: String,
                         private val jsonTransformer: JsonTransformer<T>) : CliktCommand(name = command) {
  /* Describe response in json */
  private val json by option("--json", help = "display response in json").flag()
  private val ctx by requireObject<Context>()

  override fun run() {
    try {
      val response = sendCommand(ctx.cluster!!.any().asChannel(), command, emptyList())
      when (response.status) {
        ClientACK.Codes.OK.id -> display(response.response)
        ClientACK.Codes.ERROR.id -> throw Exception(response.response)
      }
    } catch (e: Exception) {
      echo(e.message, err = true)
    }
  }

  private fun display(response: String) {
    if (json) {
      echo(response)
    } else {
      echo(jsonTransformer(response))
    }
  }
}

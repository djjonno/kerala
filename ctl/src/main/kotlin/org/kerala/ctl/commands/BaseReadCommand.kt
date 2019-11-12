package org.kerala.ctl.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.kerala.ctl.Context
import org.kerala.ctl.sendCommand
import org.kerala.shared.client.ClientACK

typealias JsonTransformer<T> = (jsonString: String) -> T

class BaseReadCommand<T>(val command: String,
                         val jsonTransformer: JsonTransformer<T>) : CliktCommand(name = command) {
  /* Describe response in json */
  val json by option("--json", help = "display response in json").flag()

  override fun run() {
    try {
      val response = sendCommand(Context.channel!!, command, emptyList())
      when (response.status) {
        ClientACK.Codes.OK.id -> display(response.response)
        ClientACK.Codes.ERROR.id -> throw Exception("`$command` invalid -> ${response.response}")
      }
    } catch (e: Exception) {
      echo("command failed, please retry -> error=\"${e.message}\"", err = true)
    }
  }

  fun display(response: String) {
    if (json) {
      echo(response)
    } else {
      val readTopics: T = jsonTransformer(response)
      echo(readTopics)
    }
  }
}
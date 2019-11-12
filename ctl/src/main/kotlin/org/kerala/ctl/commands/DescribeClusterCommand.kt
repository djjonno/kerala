package org.kerala.ctl.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import org.kerala.ctl.Context
import org.kerala.ctl.sendCommand
import org.kerala.shared.client.ClientACK
import org.kerala.shared.client.ClusterDescription
import org.kerala.shared.json.GsonUtils

class DescribeClusterCommand : CliktCommand(name = "describe-cluster") {
  val json by option("--json", help = "display response in json").flag()

  override fun run() {
    try {
      val response = sendCommand(Context.channel!!, "describe-cluster", emptyList())
      when (response.status) {
        ClientACK.Codes.OK.id -> display(response.response)
        ClientACK.Codes.ERROR.id -> throw Exception("cluster-info call failed: ${response.response}")
      }
    } catch (e: Exception) {
      echo("${e.message}", err = true)
    }
  }

  private fun display(response: String) {
    if (json) {
      echo(response)
    } else {
      val clusterInfo = GsonUtils.buildGson().fromJson(response, ClusterDescription::class.java)
      echo(clusterInfo)
    }
  }
}

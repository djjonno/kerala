package org.kerala.core.config

import com.google.common.annotations.VisibleForTesting
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.HelpFormatter
import org.apache.commons.cli.Option
import org.apache.commons.cli.Options
import org.apache.commons.cli.ParseException

import java.util.HashMap

class ConfigCliSource @VisibleForTesting
internal constructor(args: Array<String>, options: Array<Option>) : Source {

  private val options = Options()
  private val config = HashMap<String, String>()

  internal constructor(args: Array<String>) : this(args, arrayOf<Option>(CLUSTER, DATA_DIR, HELP, HOST, PORT))

  init {
    options.forEach { this.options.addOption(it) }
    parse(args)
  }

  private fun parse(args: Array<String>) {
    try {
      val cli = DefaultParser().parse(options, args)
      if (cli.hasOption("help")) {
        showHelp()
        throw Exception()
      }

      val iterator = cli.iterator()
      while (iterator.hasNext()) {
        val next = iterator.next()
        config[convertConfigKey(next.longOpt)] = next.value
      }
    } catch (e: ParseException) {
      showHelp()
      throw e
    }
  }

  private fun showHelp() {
    HelpFormatter().printHelp("kerala-server", options)
  }

  override fun compile(): Map<String, String> {
    return config
  }

  private fun convertConfigKey(key: String): String {
    return key.replace("-".toRegex(), ".")
  }

  companion object {

    private val CLUSTER = Option.builder()
        .required(false)
        .desc("nodes to be included in your static cluster.")
        .longOpt("cluster")
        .numberOfArgs(1)
        .build()

    private val DATA_DIR = Option.builder()
        .desc("directory to store data")
        .longOpt("data-dir")
        .numberOfArgs(1)
        .build()

    private val HELP = Option.builder()
        .desc("print this menu")
        .longOpt("help")
        .build()

    private val HOST = Option.builder()
        .desc("host to bind server (defaults: localhost/0.0.0.0)")
        .longOpt("host")
        .numberOfArgs(1)
        .build()

    private val PORT = Option.builder()
        .desc("port to bind server (defaults: 9191)")
        .longOpt("port")
        .numberOfArgs(1)
        .build()
  }
}

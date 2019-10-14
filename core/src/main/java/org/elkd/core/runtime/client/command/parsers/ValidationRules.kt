package org.elkd.core.runtime.client.command.parsers

import org.elkd.core.runtime.client.command.ClientCommand

interface ValidationRule {
  fun check(command: ClientCommand): Boolean
  fun message(command: ClientCommand): String
}

class PropertyExists(private val attr: String) : ValidationRule {
  override fun check(command: ClientCommand): Boolean = attr in command.args.keys
  override fun message(command: ClientCommand) = "`$attr` is a required field"
}

class Lambda(
    private val block: (command: ClientCommand) -> Boolean,
    private val constructError: (command: ClientCommand) -> String
) : ValidationRule {
  override fun check(command: ClientCommand): Boolean = block(command)
  override fun message(command: ClientCommand) = constructError(command)
}

class PropertyRegex(
    private val property: String,
    private val regex: String
) : ValidationRule {
  override fun check(command: ClientCommand): Boolean {
    val namespace = command.args[property]
    return if (namespace != null) {
      Regex(regex).matches(namespace)
    } else false
  }

  override fun message(command: ClientCommand) = "$property `${command.args[property]}` must match $regex"
}

package org.elkd.core.runtime.client.command.parsers

import org.elkd.core.runtime.client.command.SyslogCommand

interface ValidationRule {
  fun check(command: SyslogCommand): Boolean
  fun message(command: SyslogCommand): String
}

class PropertyExists(private val attr: String) : ValidationRule {
  override fun check(command: SyslogCommand): Boolean = attr in command.args.keys
  override fun message(command: SyslogCommand) = "`$attr` is a required field"
}

class Lambda(private val block: (command: SyslogCommand) -> Boolean,
             private val constructError: (command: SyslogCommand) -> String): ValidationRule {
  override fun check(command: SyslogCommand): Boolean = block(command)
  override fun message(command: SyslogCommand) = constructError(command)
}

class PropertyRegex(private val property: String,
                    private val regex: String): ValidationRule {
  override fun check(command: SyslogCommand): Boolean {
    val namespace = command.args[property]
    return if (namespace != null) {
      Regex(regex).matches(namespace)
    } else false
  }

  override fun message(command: SyslogCommand) = "$property `${command.args[property]}` must match $regex"
}

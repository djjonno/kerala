package org.elkd.core.runtime.client.controller

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

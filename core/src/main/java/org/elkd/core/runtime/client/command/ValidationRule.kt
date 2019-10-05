package org.elkd.core.runtime.client.command

interface ValidationRule {
  fun check(command: Command): Boolean
  fun message(command: Command): String
}

class PropertyExists(private val attr: String) : ValidationRule {
  override fun check(command: Command): Boolean = attr in command.args.keys
  override fun message(command: Command) = "`$attr` is a required field"
}

class Lambda(private val block: (command: Command) -> Boolean,
             private val constructError: (command: Command) -> String): ValidationRule {
  override fun check(command: Command): Boolean = block(command)
  override fun message(command: Command) = constructError(command)
}

package org.kerala.core.runtime.client.command.parsers

import org.kerala.core.runtime.client.command.ClientCommand

/**
 * A ValidationRule provides a simple mechanism to extend
 * command request validation, to ensure a given command
 * contains all the necessary parameters with valid contents.
 */
interface ValidationRule {
  /**
   * This is called to determine whether the ClientCommand
   * passes / fails this particular ValidationRule.
   *
   * @return true iff command passes the ValidationRule
   */
  fun check(command: ClientCommand): Boolean

  /**
   * The message to return if the command fails this
   * ValidationRule.
   */
  fun message(command: ClientCommand): String
}

/**
 * Check if a property exists.
 */
class PropertyExists(private val attr: String) : ValidationRule {
  override fun check(command: ClientCommand): Boolean = attr in command.args.keys
  override fun message(command: ClientCommand) = "`$attr` is a required field"
}

/**
 * Check if a property matches the given regex.
 */
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

/**
 * Run a lambda to perform dynamic validation.
 */
class Lambda(
    private val block: (command: ClientCommand) -> Boolean,
    private val constructError: (command: ClientCommand) -> String
) : ValidationRule {
  override fun check(command: ClientCommand): Boolean = block(command)
  override fun message(command: ClientCommand) = constructError(command)
}

package org.kerala.core.config

internal interface Source {
  fun compile(): Map<String, String>
}

package org.ravine.core.config

internal interface Source {
  fun compile(): Map<String, String>
}

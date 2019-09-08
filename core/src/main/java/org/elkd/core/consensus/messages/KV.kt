package org.elkd.core.consensus.messages

data class KV(val key: String, val `val`: String) {
  override fun toString(): String {
    return "KV($key, $`val`)"
  }
}

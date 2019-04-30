package org.elkd.core.server.cluster

import com.google.common.base.Preconditions
import org.elkd.shared.annotations.Mockable
import org.elkd.shared.schemes.URI

import java.util.Objects

@Mockable
class Node(uri: URI) {
  val URI: URI = Preconditions.checkNotNull(uri, "URI")

  val id: String
    get() = "node($URI)"

  val host: String
    get() = URI.host

  val port: Int
    get() = URI.port

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other == null || javaClass != other.javaClass) {
      return false
    }
    val node = other as Node?
    return URI == node!!.URI
  }

  override fun hashCode(): Int {
    return Objects.hash(URI)
  }

  override fun toString(): String {
    return id
  }
}

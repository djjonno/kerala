package org.elkd.core.server.cluster

import com.google.common.base.Preconditions
import java.util.Objects
import org.elkd.shared.annotations.Mockable
import org.elkd.shared.schemes.URI

@Mockable
class Node(uri: URI) {
  val uri: URI = Preconditions.checkNotNull(uri, "uri")

  val id: String
    get() = uri.toString()

  val host: String
    get() = uri.host

  val port: Int
    get() = uri.port

  override fun equals(other: Any?): Boolean {
    if (this === other) {
      return true
    }
    if (other == null || javaClass != other.javaClass) {
      return false
    }
    val node = other as Node?
    return uri == node!!.uri
  }

  override fun hashCode(): Int {
    return Objects.hash(uri)
  }

  override fun toString(): String {
    return id
  }
}

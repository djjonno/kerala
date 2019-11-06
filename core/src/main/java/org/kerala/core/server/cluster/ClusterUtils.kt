package org.kerala.core.server.cluster

import org.kerala.core.Environment
import org.kerala.core.config.Config
import org.kerala.shared.schemes.URI

object ClusterUtils {
  fun buildSelfNode(): Node {
    val host: String = Environment.config[Config.KEY_HOST]
    val port: Int = Environment.config[Config.KEY_PORT]
    return Node(URI.parseURIString("$host:$port"))
  }
}

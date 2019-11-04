package org.ravine.core.server.cluster

import org.ravine.core.Environment
import org.ravine.core.config.Config
import org.ravine.shared.schemes.URI

object ClusterUtils {
  fun buildSelfNode(): Node {
    val host: String = Environment.config[Config.KEY_HOST]
    val port: Int = Environment.config[Config.KEY_PORT]
    return Node(URI.parseURIString("$host:$port"))
  }
}

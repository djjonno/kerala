package org.elkd.core.server.converters

import org.elkd.core.client.model.ClientCommandRequest
import org.elkd.core.server.client.RpcClientCommandRequest

class ClientCommandRequestConverter : Converter {
  override fun forTypes() = setOf(
      RpcClientCommandRequest::class.java,
      ClientCommandRequest::class.java
  )

  override fun <T : Any?> convert(source: Any?, registry: ConverterRegistry?): T {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
  }
}

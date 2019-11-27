package org.kerala.ctl

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import org.kerala.shared.client.CtlClusterDescription
import org.kerala.shared.client.CtlNode

data class Context(var cluster: CtlClusterDescription? = null)

fun CtlClusterDescription.any(): CtlNode {
  return nodes.first()
}

fun CtlClusterDescription.leader(): CtlNode? {
  return nodes.find { it.leader }
}

fun CtlNode.asChannel(): ManagedChannel = ManagedChannelBuilder
    .forAddress(host, port)
    .usePlaintext()
    .build()

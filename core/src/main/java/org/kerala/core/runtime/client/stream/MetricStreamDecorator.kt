package org.kerala.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import org.kerala.shared.logger
import kotlin.system.measureTimeMillis

class MetricStreamDecorator<T>(private val next: StreamObserver<T>) : StreamObserver<T> {
  override fun onNext(value: T) {
    val time = measureTimeMillis { next.onNext(value) }
    logger("stream process time ${time}ms")
  }

  override fun onError(t: Throwable?) {
    next.onError(t)
  }

  override fun onCompleted() {
    next.onCompleted()
  }
}

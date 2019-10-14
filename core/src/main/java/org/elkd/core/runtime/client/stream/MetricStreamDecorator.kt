package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import kotlin.system.measureTimeMillis
import org.apache.log4j.Logger

class MetricStreamDecorator<T>(private val next: StreamObserver<T>) : StreamObserver<T> {
  override fun onNext(value: T) {
    LOGGER.info("stream process time ${measureTimeMillis { next.onNext(value) }}ms")
  }

  override fun onError(t: Throwable?) {
    next.onError(t)
  }

  override fun onCompleted() {
    next.onCompleted()
  }

  companion object {
    private val LOGGER = Logger.getLogger(MetricStreamDecorator::class.java)
  }
}

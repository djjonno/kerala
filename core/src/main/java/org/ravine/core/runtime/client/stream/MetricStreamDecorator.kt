package org.ravine.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import kotlin.system.measureTimeMillis
import org.apache.log4j.Logger

class MetricStreamDecorator<T>(private val next: StreamObserver<T>) : StreamObserver<T> {
  override fun onNext(value: T) {
    val time = measureTimeMillis { next.onNext(value) }
    LOGGER.info("stream process time ${time}ms")
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

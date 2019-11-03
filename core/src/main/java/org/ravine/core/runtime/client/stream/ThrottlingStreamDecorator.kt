package org.ravine.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger

private val LOGGER = Logger.getLogger(ThrottlingStreamDecorator::class.java)

class ThrottlingStreamDecorator<T>(
    stream: StreamObserver<T>,
    private val sleep: Long = 1
) : StreamObserver<T> by StreamObserverDecorator(
    stream,
    onNextBlock = { _, value ->
      LOGGER.info("throttling")
      Thread.sleep(sleep)
      stream.onNext(value)
    }
)

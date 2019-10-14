package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver
import org.apache.log4j.Logger

private val LOGGER = Logger.getLogger(ThrottlingStreamDecorator::class.java)

class ThrottlingStreamDecorator<T>(
    private val stream: StreamObserver<T>,
    private val sleep: Long = 1
) : StreamObserver<T> by StreamObserverDecorator(
    stream,
    onNextBlock = {
      LOGGER.info("throttling")
      Thread.sleep(sleep)
      stream.onNext(it)
    }
)

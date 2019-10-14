package org.elkd.core.runtime.client.stream

import io.grpc.stub.StreamObserver

class StreamObserverDecorator<T>(
    val stream: StreamObserver<T>,
    val onNextBlock: (T) -> Unit = { stream.onNext(it) },
    val onErrorBlock: (Throwable) -> Unit = { stream.onError(it) },
    val onCompleteBlock: () -> Unit = { stream.onCompleted() }
) : StreamObserver<T> {
  override fun onNext(value: T) = onNextBlock(value)

  override fun onError(t: Throwable) = onErrorBlock(t)

  override fun onCompleted() = onCompleteBlock()
}
